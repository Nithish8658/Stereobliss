/* */

package com.groza.Stereobliss.adapter;

/**
 * Interface used for speed optimizations on asynchronous cover loading.
 * This is necessary to load covers only at certain scroll speed to not put
 * to much load on the CPU during scrolling.
 */
public interface ScrollSpeedAdapter {

    /**
     * Sets the scrollspeed in items per second.
     *
     * @param speed Items per seconds as Integer.
     */
    void setScrollSpeed(int speed);

    /**
     * Returns the smoothed average loading time of images.
     * This value is used by the scrollspeed listener to determine if
     * the scrolling is slow enough to render images (artist, album images)
     *
     * @return Average time to load an image in ms
     */
    long getAverageImageLoadTime();

    /**
     * This method adds new loading times to the smoothed average.
     * Should only be called from the async cover loader.
     *
     * @param time Time in ms to load a image
     */
    void addImageLoadTime(long time);

}
