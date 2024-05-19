package dev.mattware.slimebuckets;

public interface PlayerCustomData {
    default boolean slimeBuckets$isInSlimeChunk() {
        return false;
    }

    default void slimeBuckets$setIsInSlimeChunk(boolean newValue) { }
}
