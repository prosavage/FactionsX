package net.prosavage.factionsx.map;

public final class WebPaint {
    /**
     * {@link String} of our fill color.
     */
    public final int fillColor;

    /**
     * {@link Double} opacity of the filled color.
     */
    public final double fillOpacity;

    /**
     * {@link String} color to be used as stroke.
     */
    public final int strokeColor;

    /**
     * {@link Integer} weight of the stroke.
     */
    public final int strokeWeight;

    /**
     * {@link Double}
     */
    public final double strokeOpacity;

    /**
     * Constructor.
     * Initialize our corresponding fields.
     *
     * @param fillColorHex   {@link Integer} hex color code of our filling.
     * @param fillOpacity    {@link Double} opacity to be used for our filling.
     * @param strokeColorHex {@link Integer} hex color code of our stroke.
     * @param strokeWeight   {@link Integer} weight of the stroke to be used.
     * @param strokeOpacity  {@link Double} opacity to be used on our stroke.
     */
    public WebPaint(
            final int fillColorHex,
            final double fillOpacity,
            final int strokeColorHex,
            final int strokeWeight,
            final double strokeOpacity
    ) {
        this.fillColor = fillColorHex;
        this.fillOpacity = fillOpacity;
        this.strokeColor = strokeColorHex;
        this.strokeWeight = strokeWeight;
        this.strokeOpacity = strokeOpacity;
    }
}