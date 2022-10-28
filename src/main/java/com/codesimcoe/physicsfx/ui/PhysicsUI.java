package com.codesimcoe.physicsfx.ui;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.codesimcoe.physicsfx.configuration.Configuration;
import com.codesimcoe.physicsfx.domain.GravityObject;
import com.codesimcoe.physicsfx.domain.Particle;
import com.codesimcoe.physicsfx.drawing.DrawingUtil;
import com.codesimcoe.physicsfx.model.Model;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PhysicsUI {

    private static final Effect NO_EFFECT = null;

    private final Model model = Model.getInstance();
    private final Configuration configuration = Configuration.getInstance();

    private final Pane root;

    private final GraphicsContext graphicsContext;

    private double mouseX;
    private double mouseY;

    private final Random random = new Random();

    private ScheduledFuture<?> particleGenerationFuture;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PhysicsUI() {
        this.root = new Pane();

        Canvas canvas = new Canvas(Configuration.CANVAS_WIDTH, Configuration.CANVAS_HEIGHT);
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.root.getChildren().add(canvas);

        this.graphicsContext.setGlobalBlendMode(BlendMode.SRC_OVER);

        this.root.setOnMousePressed(e -> {
            this.mouseX = e.getX();
            this.mouseY = e.getY();

            MouseButton button = e.getButton();
            switch (button) {
                case PRIMARY:
                    this.startGeneratingParticles();
                    break;
                case SECONDARY:
                    GravityObject repulsor = new GravityObject(this.mouseX, this.mouseY, false);
                    this.model.addGravityObject(repulsor);
                    break;
                case MIDDLE:
                    GravityObject attractor = new GravityObject(this.mouseX, this.mouseY, true);
                    this.model.addGravityObject(attractor);
                    break;
                default:
                    break;
            }
        });

        this.root.setOnMouseDragged(e -> {
            this.mouseX = e.getX();
            this.mouseY = e.getY();
        });

        this.root.setOnMouseReleased(e -> {
            this.stopGeneratingParticles();
        });

        // Configuration update
        this.configuration.getGaussianBlurEffect().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Effects
                this.graphicsContext.setEffect(new GaussianBlur());
            } else {
                this.graphicsContext.setEffect(NO_EFFECT);
            }
        });
    }

    private void startGeneratingParticles() {
        this.particleGenerationFuture = this.scheduler.scheduleAtFixedRate(
            () -> {
                int amount = this.configuration.getParticleGenerationAmount().get();
                Platform.runLater(() -> {
                    for (int i = 0; i < amount; i++) {
                        this.generateParticle(this.mouseX, this.mouseY);
                    }
                });
            },
            0,
            this.configuration.getParticleGenerationPeriodMs().get(),
            TimeUnit.MILLISECONDS
        );
    }

    private void stopGeneratingParticles() {
        if (this.particleGenerationFuture != null) {
            this.particleGenerationFuture.cancel(true);
        }
    }

    private void generateParticle(final double x, final double y) {

        int maxSpeed = 10;

        double speed = this.random.nextDouble() * maxSpeed;
        double direction = 2 * Math.PI * this.random.nextDouble();

        double vx = speed * Math.cos(direction);
        double vy = speed * Math.sin(direction);

        // Use a random color
        double hue = 360 * this.random.nextDouble();
        Color color = Color.hsb(hue, 1.0, 0.8, 0.75);
        Particle particle = new Particle(x, y, vx, vy, color);
        this.model.addParticle(particle);
    }

    public void update() {

        int gravityStrength = this.configuration.getGravityStrength().get();

        this.model.getParticles().forEach(particle -> {

            double ax = 0;
            double ay = 0;

            for (GravityObject gravityObject : this.model.getGravityObjets()) {

                double dx = particle.getX() - gravityObject.x();
                double dy = particle.getY() - gravityObject.y();

                double squaredDistance = dx * dx + dy * dy;
                double force = Math.min(2, gravityStrength / squaredDistance);
                double direction = Math.atan2(dy, dx);

                double dax = force * Math.cos(direction);
                double day = force * Math.sin(direction);

                if (gravityObject.attractor()) {
                    // Attracts
                    ax -= dax;
                    ay -= day;
                } else {
                    // Repulses
                    ax += dax;
                    ay += day;
                }
            }

            particle.setAx(ax);
            particle.setAy(ay);

            particle.update();
        });
    }

    public void draw() {

        // Clear
        this.graphicsContext.clearRect(0, 0, Configuration.CANVAS_WIDTH, Configuration.CANVAS_HEIGHT);

        double radius = this.configuration.getGravityStrength().get() / 35.0;

        this.model.getGravityObjets().forEach(gravityObject -> {

            Color color = gravityObject.attractor() ? Color.CORNFLOWERBLUE : Color.ORANGERED;
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setFill(color);

            // Gravity representation
            DrawingUtil.drawCircle(
                gravityObject.x(),
                gravityObject.y(),
                radius,
                0.4,
                0.6,
                this.graphicsContext
            );
        });

        // Iterator used as we'll remove items while iterating
        Iterator<Particle> iterator = this.model.getParticles().iterator();

        while (iterator.hasNext()) {
            Particle p = iterator.next();

            int life = p.getLife();

            double ratio = (double) life / p.getInitialLife();
            this.graphicsContext.setGlobalAlpha(ratio);
            this.graphicsContext.setFill(p.getColor());
            this.graphicsContext.fillOval(p.getX(), p.getY(), 4, 4);

            // Dead particle
            if (life <= 1) {
                iterator.remove();
            }
        }

        this.graphicsContext.setGlobalAlpha(1);
    }

    public Node getNode() {
        return this.root;
    }
}