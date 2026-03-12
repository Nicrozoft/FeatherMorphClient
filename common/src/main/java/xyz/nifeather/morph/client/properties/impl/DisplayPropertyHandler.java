package xyz.nifeather.morph.client.properties.impl;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Vector3f;
import xyz.nifeather.morph.client.mixin.accessors.DisplayAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class DisplayPropertyHandler extends EntityPropertyHandler<Display>
{
    public final ClientProperty<Float, Display> CULLBOX_WIDTH =
            ClientProperty.builder(PropertyNames.DISPLAY_WIDTH, -1f, Display.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setWidth)
                    .build();

    public final ClientProperty<Float, Display> CULLBOX_HEIGHT =
            ClientProperty.builder(PropertyNames.DISPLAY_HEIGHT, -1f, Display.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setHeight)
                    .build();

    public final ClientProperty<Vector3f, Display> SCALE =
            ClientProperty.builder(PropertyNames.DISPLAY_SCALE, new Vector3f(1), Display.class)
                    .inputHandle(CommonInputHandles::readVector3fRelaxed)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle((display, scale) ->
                    {
                        var translation = display.getEntityData().get(((DisplayAccessor)display).getDATA_TRANSLATION_ID());
                        display.setTransformation(new Transformation(translation, null, scale, null));
                    })
                    .build();

    public final ClientProperty<Integer, Display> GLOW_COLOR =
            ClientProperty.builder(PropertyNames.DISPLAY_GLOW_COLOR, 0, Display.class)
                    .inputHandle(CommonInputHandles::readHexColor)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setGlowColorOverride)
                    .build();

    public final ClientProperty<Float, Display> SHADOW_RADIUS =
            ClientProperty.builder(PropertyNames.DISPLAY_SHADOW_RADIUS, 0f, Display.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setShadowRadius)
                    .build();

    public final ClientProperty<Float, Display> SHADOW_STRENGTH =
            ClientProperty.builder(PropertyNames.DISPLAY_SHADOW_STRENGTH, 0f, Display.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setShadowStrength)
                    .build();

    public final ClientProperty<Integer, Display> LIGHT_OVERRIDE =
            ClientProperty.builder(PropertyNames.DISPLAY_LIGHT_OVERRIDE, 15728880, Display.class)
                    .inputHandle(CommonInputHandles::readLight)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle((display, packedLight) -> display.setBrightnessOverride(Brightness.unpack(packedLight)))
                    .build();

    public final ClientProperty<Vector3f, Display> TRANSLATION =
            ClientProperty.builder(PropertyNames.DISPLAY_TRANSLATION, new Vector3f(0), Display.class)
                    .inputHandle(CommonInputHandles::readVector3fRelaxed)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle((display, translation) ->
                    {
                        var scale = display.getEntityData().get(((DisplayAccessor)display).getDATA_SCALE_ID());
                        display.setTransformation(new Transformation(translation, null, scale, null));
                    })
                    .build();

    public final ClientProperty<Display.BillboardConstraints, Display> BILLBOARD =
            ClientProperty.builder(PropertyNames.DISPLAY_BILLBOARD, Display.BillboardConstraints.FIXED, Display.class)
                    .inputHandle(this::readBillboard)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display::setBillboardConstraints)
                    .build();

    private Optional<Display.BillboardConstraints> readBillboard(String input)
    {
        return CommonInputHandles.readEnum(Display.BillboardConstraints.values(), input);
    }

    public DisplayPropertyHandler()
    {
        register(
                CULLBOX_HEIGHT, CULLBOX_WIDTH, SCALE, GLOW_COLOR, SHADOW_RADIUS, SHADOW_STRENGTH, LIGHT_OVERRIDE, TRANSLATION, BILLBOARD
        );
    }
}
