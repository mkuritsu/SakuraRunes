package io.github.itstaylz.sakurarunes.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public final class ParticleUtils {

    public static void drawCircleParticles(Location location, Particle particle, double radius, int precision) {
        for (int i = 0; i < 360; i += precision) {
            double angle = Math.toRadians(i);
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Vector offset = new Vector(x, 0, z);
            location.add(offset);
            location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0);
            location.subtract(offset);
        }
    }

    public static void drawSquareParticles(Location location, Particle particle) {
        Vector vector = new Vector(0, 0, 0.1);
        for (int j = 0; j < 4; j++) {
            // Line
            for (int k = 0; k < 10; k++) {
                location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0);
                location = location.add(vector);
            }
            vector.rotateAroundY(Math.PI / 2);
        }
    }
}
