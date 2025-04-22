package dev.mattware.slimebuckets.accessors;

import org.joml.Vector3f;

public interface AbstractClientPlayerMixinAccess {
    Vector3f slimebuckets$getLastMainHandPos();
    Vector3f slimebuckets$getLastOffHandPos();
    void slimebuckets$setLastMainHandPos(Vector3f pos);
    void slimebuckets$setLastOffHandPos(Vector3f pos);
}
