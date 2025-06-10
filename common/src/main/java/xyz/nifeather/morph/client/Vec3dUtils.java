package xyz.nifeather.morph.client;

import net.minecraft.world.phys.Vec3;

public class Vec3dUtils {
    public static Vec3 of(Vec3 other) {
        return of(other.x, other.y, other.z);
    }

    public static Vec3 of(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static Vec3 of(double value) {
        return new Vec3(value, value, value);
    }

    public static double horizontalSquaredDistance(Vec3 vec1, Vec3 vec2) {
        var xDiff = vec1.x - vec2.x;
        var zDiff = vec1.z - vec2.z;

        return xDiff * xDiff + zDiff * zDiff;
    }

    public static Vec3 ONE() {
        return of(1);
    }
}
