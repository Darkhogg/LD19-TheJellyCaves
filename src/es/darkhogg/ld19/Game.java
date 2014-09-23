package es.darkhogg.ld19;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public final class Game extends Canvas implements Runnable, KeyListener {

    protected static final int WIDTH = 320;
    protected static final int HEIGHT = 240;
    protected static final int SCALE = 2;
    protected static final int FPS = 60;

    Thread gameThread;
    long now;
    private long lastFrame, lastMove, lastDig, lastEnemy, lastAttack, lastGc, lastGameOver, lastHit;
    private boolean canSleep = true;

    private boolean paused, justPaused;

    private volatile boolean keyUp, keyDown, keyRight, keyLeft, keyDig, keyChest, keyAttack, keyPause, keyScreenshot;
    private long screenshotTicks;
    private boolean gameOver, titleScreen = true;
    private volatile boolean running;

    Cave currentCave;
    Player player;
    int score = 0;
    int rocks;
    long ticks;

    protected int camX, camY;

    double calcFps;

    String message = null;
    long lastMessage;

    Map<SpriteEntity,Long> treasureEntities = new HashMap<SpriteEntity,Long>();
    Collection<Enemy> enemies = new HashSet<Enemy>();
    Collection<Arrow> arrows = new HashSet<Arrow>();
    Collection<Particle> particles = new LinkedList<Particle>();
    Collection<Heart> hearts = new HashSet<Heart>();
    Collection<Ball> balls = new HashSet<Ball>();

    public Game () {
        super();

        setSize(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(getSize());
        addKeyListener(this);
        requestFocus();
    }

    @Override
    public void run () {

        while (running) {
            try {
                long st = System.nanoTime();
                tick();
                waitFrame();
                long nd = System.nanoTime();
                calcFps = 1d / ((nd - st) / 1000000000d);
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(this, e);
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

    private void tick () {
        update();
        display();

        if (now - lastGc > 10000000000L) {
            System.gc();
            lastGc = now;
        }

        ticks++;

        // Screenshot
        if (keyScreenshot) {
            screenshotTicks++;
        } else {
            screenshotTicks = 0;
        }
    }

    private void update () {
        now = System.nanoTime();

        final long MOVE_TIME = 150000000;
        final long ACTION_TIME = 250000000;

        if (!gameOver && !titleScreen) {
            if (keyPause && !justPaused) {
                justPaused = true;
                paused ^= true;
            }
        } else {
            paused = false;
        }
        if (!keyPause && justPaused) {
            justPaused = false;
        }

        if (titleScreen) {
            if (keyDig) {
                currentCave = Cave.generateCave(32, 32);
                player = new Player(this, currentCave.iniX * 16, currentCave.iniY * 16);
                titleScreen = false;
                canSleep = true;
                score = 0;
                rocks = 8;

                Sound.START.play();
                particles.clear();
            }
        }

        // Movement
        if (!gameOver && !titleScreen && !paused) {
            if (now - lastMove >= MOVE_TIME) {
                final int MOVE = 16;

                // Move Up
                if (keyUp && currentCave.getTerrainAt((player.posX) / 16, (player.posY - MOVE) / 16).isWalkable()) {
                    player.posY -= MOVE;
                }

                // Move Down
                if (keyDown && currentCave.getTerrainAt((player.posX) / 16, (player.posY + MOVE) / 16).isWalkable()) {
                    player.posY += MOVE;
                }

                // Move Left
                if (keyLeft && currentCave.getTerrainAt((player.posX - MOVE) / 16, (player.posY) / 16).isWalkable()) {
                    player.posX -= MOVE;
                }

                // Move Right
                if (keyRight && currentCave.getTerrainAt((player.posX + MOVE) / 16, (player.posY) / 16).isWalkable()) {
                    player.posX += MOVE;
                }
            }

            if (keyUp || keyDown || keyLeft || keyRight) {
                if (now - lastMove >= MOVE_TIME) {
                    lastMove = now;
                }
                currentCave.calcLight(player.posX / 16, player.posY / 16);
            }

            // Update player facing
            if (keyUp && !keyDown) {
                player.facing = Direction.UP;
            }
            if (keyDown && !keyUp) {
                player.facing = Direction.DOWN;
            }
            if (keyRight && !keyLeft) {
                player.facing = Direction.RIGHT;
            }
            if (keyLeft && !keyRight) {
                player.facing = Direction.LEFT;
            }

            if (keyAttack && rocks > 0 && now - lastAttack > 400000000) {
                Arrow arrow = new Arrow(this, player.facing);
                arrows.add(arrow);
                arrow.posX = player.posX;
                arrow.posY = player.posY;
                lastAttack = now;
                Sound.THROW.play();
                rocks--;
            }

            // Update the player
            player.update();

            // Update arrows
            for (Iterator<Arrow> it = arrows.iterator(); it.hasNext();) {
                Arrow arrow = it.next();
                arrow.update();
                if (!currentCave.getTerrainAt(arrow.posX / 16, arrow.posY / 16).isWalkable()) {
                    it.remove();
                }
            }

            // Update particles
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle part = it.next();
                part.update();
                if (part.life < 0) {
                    it.remove();
                }
            }

            // Enemy-player collisions
            if (now - lastHit > 700000000L) {
                for (Enemy enem : enemies) {
                    if (enem.canHurt()
                        && Math.abs(enem.posX - player.posX) < 16 && Math.abs(enem.posY - player.posY) < 16)
                    {
                        // Re-facing the player
                        // POST-LD fix
                        int i = 0;
                        Direction[] dirs =
                            {
                                player.facing, player.facing.rotateLeft(), player.facing.rotateRight(),
                                player.facing.invert() };
                        do {
                            player.facing = dirs[i];
                            i++;
                        } while (!currentCave
                            .getTerrainAt(
                                player.posX / 16 - player.facing.getX(), player.posY / 16 - player.facing.getY())
                            .isWalkable()
                            && i <= 4);

                        // Move the player etc.
                        player.posX -= player.facing.getX() * 16;
                        player.posY -= player.facing.getY() * 16;
                        player.life -= enem.getDamage();
                        Sound.HURT.play();
                        lastHit = now;
                    }

                    if (player.life <= 0) {
                        gameOver = true;
                        lastGameOver = now;
                        Sound.DEATH.play();
                    }
                }
            }

            // Breaking walls
            if (keyDig && now - lastDig > ACTION_TIME) {
                int x = player.posX / 16 + player.facing.getX();
                int y = player.posY / 16 + player.facing.getY();

                if (currentCave.getTerrainAt(x, y) == TerrainType.WALL) {
                    Sound.DIG.play();
                    currentCave.terrainData[x][y] = TerrainType.GROUND;
                    currentCave.discover(x, y);
                    currentCave.calcLight(player.posX / 16, player.posY / 16);
                    lastDig = now;
                    rocks++;
                }
            }

            // Open chests
            if (keyChest) {
                int x = player.posX / 16 + player.facing.getX();
                int y = player.posY / 16 + player.facing.getY();

                if (currentCave.getTerrainAt(x, y) == TerrainType.CHEST_CLOSED) {
                    // Sound.CHEST.play();
                    currentCave.terrainData[x][y] = TerrainType.CHEST_OPEN;
                    TreasureType tt = currentCave.treasures[x][y].open();
                    message = "You have found " + tt.getPrep() + " " + tt.getName() + "!\nIt's worth $" + tt.getScore();
                    lastMessage = now;
                    score += tt.getScore();
                    SpriteEntity se = new SpriteEntity(this, tt.getSprite());
                    se.spdY = -1;
                    se.posX = x * 16;
                    se.posY = y * 16 - 8;
                    treasureEntities.put(se, now);
                    Sound.PICKUP.play();
                }
            }

            // Tick spriteentities
            for (Iterator<Map.Entry<SpriteEntity,Long>> it = treasureEntities.entrySet().iterator(); it.hasNext();) {
                Map.Entry<SpriteEntity,Long> entry = it.next();
                entry.getKey().update();
                if (now - entry.getValue().longValue() > 500000000) {
                    it.remove();
                }
            }

            // Change cave if needed
            if (currentCave.getTerrainAt(player.posX / 16, player.posY / 16) == TerrainType.INIT) {
                if (currentCave.parentCave != null) {
                    player.posX = currentCave.parentX * 16 + player.facing.getX() * 16;
                    player.posY = currentCave.parentY * 16 + player.facing.getY() * 16;
                    currentCave = currentCave.parentCave;

                    message = "Level " + currentCave.level;
                    lastMessage = now;
                    enemies.clear();
                    particles.clear();
                    hearts.clear();
                    arrows.clear();
                }
            }
            if (currentCave.getTerrainAt(player.posX / 16, player.posY / 16) == TerrainType.EXIT) {
                Point point = new Point(player.posX / 16, player.posY / 16);
                if (currentCave.subCaves.get(point) == null) {
                    currentCave.subCaves.put(
                        point, Cave.generateCave(32 + (int) (Math.random() * 256), 32 + (int) (Math.random() * 256)));

                    Cave subCave = currentCave.subCaves.get(point);
                    subCave.parentCave = currentCave;
                    subCave.parentX = point.x;
                    subCave.parentY = point.y;
                    subCave.level = currentCave.level + 1;
                }

                currentCave = currentCave.subCaves.get(point);
                player.posX = currentCave.iniX * 16 + player.facing.getX() * 16;
                player.posY = currentCave.iniY * 16 + player.facing.getY() * 16;

                message = "Level " + currentCave.level;
                lastMessage = now;
                enemies.clear();
                particles.clear();
                hearts.clear();
                arrows.clear();
            }

            // Generate Enemies
            if (enemies.size() <= Math.sqrt(currentCave.width * currentCave.height) / 8 && now - lastEnemy > 5000000L) {
                lastEnemy = now;
                int tryX = (int) (Math.random() * currentCave.width) * 16;
                int tryY = (int) (Math.random() * currentCave.height) * 16;
                int difX = tryX - player.posX;
                int difY = tryY - player.posY;
                if (currentCave.getLightAt(tryX / 16, tryY / 16) < 0.2
                    && currentCave.getTerrainAt(tryX / 16, tryY / 16).isWalkable()
                    && difX * difX + difY * difY < 512 * 512)
                {
                    Enemy enem = Enemy.getForLevel(this, currentCave.level);
                    enem.posX = tryX;
                    enem.posY = tryY;
                    enemies.add(enem);
                    if (currentCave.isDiscovered(tryX / 16, tryY / 16)) {
                        for (int i = 0; i < 25; i++) {
                            double x = tryX + Math.random() * 16 - 8;
                            double y = tryY + Math.random() * 16 - 8;
                            double xs = -0.25 + Math.random() * 0.5;
                            double ys = -0.25 + Math.random() * 0.5;
                            Sprite spr = null;
                            int life = 0;
                            if (i < 5) {
                                spr = Sprite.PUFF_BIG;
                                life = (int) (Math.random() * 10);
                            } else if (i < 15) {
                                spr = Sprite.PUFF_MEDIUM;
                                life = (int) (10 + Math.random() * 20);
                            } else {
                                spr = Sprite.PUFF_SMALL;
                                life = (int) (20 + Math.random() * 30);
                            }
                            particles.add(new Particle(this, x, y, xs, ys, 0.0, -0.01, spr, life));
                        }
                    }
                }
            }

            // Collision arrow-enemy
            for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext();) {
                Enemy enem = eit.next();
                for (Iterator<Arrow> ait = arrows.iterator(); ait.hasNext();) {
                    Arrow arrow = ait.next();
                    if (enem.canBeHurt()
                        && Math.abs(enem.posX - arrow.posX) < 16 && Math.abs(enem.posY - arrow.posY) < 16)
                    {
                        enem.life--;
                        ait.remove();
                        Sound.HURT.play();
                    }
                }
            }

            // Collision player-heart
            for (Iterator<Heart> it = hearts.iterator(); it.hasNext();) {
                Heart heart = it.next();
                if (Math.abs(heart.posX - player.posX) < 16 && Math.abs(heart.posY - player.posY) < 16) {
                    player.life = Math.min(20, player.life + 2);
                    it.remove();
                    Sound.HEART.play();
                }
            }

            // Update enemies
            for (Iterator<Enemy> it = enemies.iterator(); it.hasNext();) {
                Enemy enem = it.next();
                enem.update();
                int difX = player.posX - enem.posX;
                int difY = player.posY - enem.posY;
                if (difX * difX + difY * difY > 576 * 576) {
                    it.remove();
                }
                if (enem.life <= 0) {
                    it.remove();
                    Sound.ENEMY_DEATH.play();
                    if (Math.random() < enem.getHeartProbability()) {
                        Heart h = new Heart(this, enem.posX, enem.posY);
                        hearts.add(h);
                    }
                    for (int i = 0; i < 25; i++) {
                        double x = enem.posX + Math.random() * 16 - 8;
                        double y = enem.posY + Math.random() * 16 - 8;
                        double xs = -0.25 + Math.random() * 0.5;
                        double ys = -0.25 + Math.random() * 0.5;
                        Sprite spr = null;
                        int life = 0;
                        if (i < 5) {
                            spr = Sprite.PUFF_BIG;
                            life = (int) (Math.random() * 10);
                        } else if (i < 15) {
                            spr = Sprite.PUFF_MEDIUM;
                            life = (int) (10 + Math.random() * 20);
                        } else {
                            spr = Sprite.PUFF_SMALL;
                            life = (int) (20 + Math.random() * 30);
                        }
                        particles.add(new Particle(this, x, y, xs, ys, 0.0, -0.01, spr, life));
                    }
                }
            }

            // Update Balls
            for (Iterator<Ball> it = balls.iterator(); it.hasNext();) {
                Ball ball = it.next();
                ball.update();
                if (ball.destroy) {
                    it.remove();
                }
            }

            // Collide player-balls
            for (Iterator<Ball> it = balls.iterator(); it.hasNext();) {
                Ball ball = it.next();
                if (Math.abs(ball.posX - player.posX) < 8 && Math.abs(ball.posY - player.posY) < 8) {
                    player.life -= 1;
                    it.remove();
                    Sound.HURT.play();
                }
            }

            // Update the camera position
            camX = player.posX;
            camY = player.posY;
        }

        if (gameOver && keyDig && now - lastGameOver > 1000000000L) {
            gameOver = false;
            titleScreen = true;
        }
    }

    private void display () {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            bs = getBufferStrategy();
        }

        Graphics gr = bs.getDrawGraphics();
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        render((Graphics2D) img.getGraphics());

        gr.drawImage(img, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, 0, 0, WIDTH, HEIGHT, null);
        bs.show();

        if (screenshotTicks == 1) {
            ScreenshotHelper.saveScreenshot(img);
        }
    }

    private void render (Graphics2D g) {
        // Fill Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (titleScreen) {
            g.drawImage(Sprite.TITLE.getImage(), WIDTH / 2 - 111, 32, null);

            if (!hasFocus()) {
                Font.drawStringAt("-> Click Here <-", g, WIDTH / 2, HEIGHT / 2, 0.5);
            } else {
                Font.drawStringAt("Press V To play", g, WIDTH / 2, HEIGHT / 2, 0.5);
            }
        } else {
            // Minimum and maximum positions in the screen to draw things
            int minX = camX - (WIDTH * SCALE) / 2;
            int maxX = camX + (WIDTH * SCALE) / 2;
            int minY = camY - (HEIGHT * SCALE) / 2;
            int maxY = camY + (HEIGHT * SCALE) / 2;

            // Draw the map
            for (int i = minX / 16; i <= maxX / 16; i++) {
                for (int j = minY / 16; j <= maxY / 16; j++) {
                    if (currentCave.isDiscovered(i, j)) {
                        TerrainType tt = currentCave.getTerrainAt(i, j);
                        drawCenteredAt(g, tt.getSprite().getImage(), i * 16, j * 16);
                    }
                }
            }

            // Enemies
            for (Enemy enem : enemies) {
                if (currentCave.isDiscovered(enem.posX / 16, enem.posY / 16)) {
                    drawCenteredAt(g, enem.getSprite().getImage(), enem.posX, enem.posY);
                }
            }

            // Arrows
            for (Arrow arrow : arrows) {
                if (currentCave.isDiscovered(arrow.posX / 16, arrow.posY / 16)) {
                    drawCenteredAt(g, arrow.getSprite().getImage(), arrow.posX, arrow.posY);
                }
            }

            // Hearts
            for (Heart heart : hearts) {
                drawCenteredAt(g, Sprite.HEART_FULL.getImage(), heart.posX, heart.posY);
            }

            // Particles
            for (Particle part : particles) {
                drawCenteredAt(g, part.sprite.getImage(), (int) part.posX, (int) part.posY);
            }

            // Balls
            for (Ball ball : balls) {
                drawCenteredAt(g, Sprite.BLUE_BALL.getImage(), (int) ball.posX, (int) ball.posY);
            }

            // Shadow
            for (int i = minX / 16 - 1; i <= maxX / 16; i++) {
                for (int j = minY / 16 - 1; j <= maxY / 16; j++) {
                    if (currentCave.isDiscovered(i, j)) {
                        g.setColor(new Color(0, 0, 0, (int) ((0.8 - 0.8 * currentCave.getLightAt(i, j)) * 255)));
                        int x = i * 16 - camX + WIDTH / 2 - 8;
                        int y = j * 16 - camY + HEIGHT / 2 - 8;
                        g.fillRect(Math.max(x, 0), Math.max(y, 0), x + 16 - Math.max(x, 0), y + 16 - Math.max(y, 0));
                    }
                }
            }

            // Draw the player
            if (now - lastHit > 700000000L || ticks % 2 == 0) {
                drawCenteredAt(g, player.getSprite().getImage(), player.posX, player.posY);
            }

            // Render treasures
            for (SpriteEntity se : treasureEntities.keySet()) {
                if (se.sprite != null) {
                    drawCenteredAt(g, se.sprite.getImage(), se.posX, se.posY);
                }
            }

            // Draw message
            if (message != null && now - lastMessage < 3000000000L) {
                Font.drawStringAt(message, g, WIDTH / 2, HEIGHT - 32, 0.5);
            }

            // Draw score & level & rocks
            Font.drawStringAt("$" + score, g, 4, 4, 0.0);
            Font.drawStringAt("Level " + currentCave.level, g, WIDTH - 4, 4, 1.0);
            Font.drawStringAt(String.valueOf(rocks), g, 16, 16, 0.0);
            g.drawImage(Sprite.ARROW_UP.getImage(), 0, 12, null);

            // Draw player life
            int lifeX = WIDTH / 2 - 45;
            int lifeY = HEIGHT - 12;
            for (int i = 0; i < 10; i++) {
                if (player.life > i * 2) {
                    g.drawImage(Sprite.HEART_FULL.getImage(), i * 9 + lifeX, lifeY, null);
                }
                if (player.life <= i * 2) {
                    g.drawImage(Sprite.HEART_EMPTY.getImage(), i * 9 + lifeX, lifeY, null);
                }
                if (player.life == i * 2 + 1) {
                    g.drawImage(Sprite.HEART_HALF.getImage(), i * 9 + lifeX, lifeY, null);
                }
            }

            // If the game doesn't have focus, draw a message indicating it
            if (!hasFocus()) {
                g.setColor(new Color(0, 0, 0, (int) (0.5 * 255)));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                Font.drawStringAt("-> Click Here <-", g, WIDTH / 2, HEIGHT / 2, 0.5);
            } else if (gameOver) {
                g.setColor(new Color(0, 0, 0, (int) (0.5 * 255)));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                Image img = Sprite.GAME_OVER.getImage();
                g.drawImage(img, (WIDTH - img.getWidth(null)) / 2, (HEIGHT - img.getHeight(null)) / 2 - 16, null);

                Font.drawStringAt("Final money: $" + score, g, WIDTH / 2, HEIGHT / 2 + 16, 0.5);
            } else if (paused) {
                g.setColor(new Color(0, 0, 0, (int) (0.5 * 255)));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                Font.drawStringAt("Game paused", g, WIDTH / 2, HEIGHT / 2, 0.5);
            }
        }

        // Draw FPS
        g.setColor(Color.WHITE);
        // g.drawString( Float.toString( Math.round( calcFps*100 )/100 ), 1, HEIGHT-5 );
    }

    protected void drawCenteredAt (Graphics2D g, Image img, int x, int y) {
        int w = img.getWidth(null);
        int h = img.getWidth(null);

        // Center of the screen
        int ctX = WIDTH / 2;
        int ctY = HEIGHT / 2;

        // If the camera lets us view it
        g.drawImage(img, x - camX + ctX - w / 2, y - camY + ctY - h / 2, null);
    }

    private void waitFrame () throws InterruptedException {
        long dif = 0;
        long tick = 1000000000 / FPS;

        do {
            dif = System.nanoTime() - lastFrame;
            if (canSleep && dif < 1000000) {
                Thread.sleep(1);
            }
        } while (dif < tick);

        if (dif > 100000000) {
            canSleep = false;
        }

        lastFrame = System.nanoTime();
    }

    protected void start () {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
            running = true;
        } else {
            System.err.println("ERROR: Game already started");
        }
    }

    protected void stop () {
        running = false;
    }

    @Override
    public void keyPressed (KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                keyUp = true;
                break;
            case KeyEvent.VK_DOWN:
                keyDown = true;
                break;
            case KeyEvent.VK_RIGHT:
                keyRight = true;
                break;
            case KeyEvent.VK_LEFT:
                keyLeft = true;
                break;
            case KeyEvent.VK_V:
                keyDig = true;
                break;
            case KeyEvent.VK_C:
                keyChest = true;
                break;
            case KeyEvent.VK_X:
                keyAttack = true;
                break;
            case KeyEvent.VK_ENTER:
                keyPause = true;
                break;
            case KeyEvent.VK_F12:
                keyScreenshot = true;
                break;
        }
    }

    @Override
    public void keyReleased (KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                keyUp = false;
                break;
            case KeyEvent.VK_DOWN:
                keyDown = false;
                break;
            case KeyEvent.VK_RIGHT:
                keyRight = false;
                break;
            case KeyEvent.VK_LEFT:
                keyLeft = false;
                break;
            case KeyEvent.VK_V:
                keyDig = false;
                break;
            case KeyEvent.VK_C:
                keyChest = false;
                break;
            case KeyEvent.VK_X:
                keyAttack = false;
                break;
            case KeyEvent.VK_ENTER:
                keyPause = false;
                break;
            case KeyEvent.VK_F12:
                keyScreenshot = false;
                break;
        }
    }

    @Override
    public void keyTyped (KeyEvent ke) {
        // Nothing
    }

}
