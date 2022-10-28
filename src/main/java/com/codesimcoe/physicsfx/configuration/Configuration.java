package com.codesimcoe.physicsfx.configuration;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Configuration {

    // UI
    public static final double CANVAS_WIDTH = 1200;
    public static final double CANVAS_HEIGHT = 900;

    public static final double CONFIG_WIDTH = 300;

    public static final double UI_WIDTH = CANVAS_WIDTH + CONFIG_WIDTH;
    public static final double UI_HEIGHT = 900;

    // Physics
    private final DoubleProperty slowStrength = new SimpleDoubleProperty(0.995);
    private final IntegerProperty forceStrength = new SimpleIntegerProperty(1_000);

    // Particle
    private final IntegerProperty particleLife = new SimpleIntegerProperty(200);

    // Delay between 2 particles generation
    private final IntegerProperty particleGenerationPeriodMs = new SimpleIntegerProperty(10);

    // Number of particles generated per generation period
    private final IntegerProperty particleGenerationAmount = new SimpleIntegerProperty(5);

    // Gaussian blur effect
    private final BooleanProperty gaussianBlurEffect = new SimpleBooleanProperty(false);

    // Eagerly instantiated singleton
    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    private Configuration() {
        //
    }

    public IntegerProperty getParticleGenerationAmount() {
        return this.particleGenerationAmount;
    }

    public IntegerProperty getParticleLife() {
        return this.particleLife;
    }

    public IntegerProperty getGravityStrength() {
        return this.forceStrength;
    }

    public DoubleProperty getSlowStrength() {
        return this.slowStrength;
    }

    public BooleanProperty getGaussianBlurEffect() {
        return this.gaussianBlurEffect;
    }

    public IntegerProperty getParticleGenerationPeriodMs() {
        return this.particleGenerationPeriodMs;
    }
}