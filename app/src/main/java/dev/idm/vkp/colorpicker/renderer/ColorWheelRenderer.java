package dev.idm.vkp.colorpicker.renderer;

import java.util.List;

import dev.idm.vkp.colorpicker.ColorCircle;

public interface ColorWheelRenderer {
    float GAP_PERCENTAGE = 0.025f;

    void draw();

    ColorWheelRenderOption getRenderOption();

    void initWith(ColorWheelRenderOption colorWheelRenderOption);

    List<ColorCircle> getColorCircleList();
}
