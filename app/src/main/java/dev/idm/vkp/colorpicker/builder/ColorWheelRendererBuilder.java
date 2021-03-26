package dev.idm.vkp.colorpicker.builder;

import dev.idm.vkp.colorpicker.ColorPickerView;
import dev.idm.vkp.colorpicker.renderer.ColorWheelRenderer;
import dev.idm.vkp.colorpicker.renderer.FlowerColorWheelRenderer;
import dev.idm.vkp.colorpicker.renderer.SimpleColorWheelRenderer;

public class ColorWheelRendererBuilder {
    public static ColorWheelRenderer getRenderer(ColorPickerView.WHEEL_TYPE wheelType) {
        switch (wheelType) {
            case CIRCLE:
                return new SimpleColorWheelRenderer();
            case FLOWER:
                return new FlowerColorWheelRenderer();
        }
        throw new IllegalArgumentException("wrong WHEEL_TYPE");
    }
}