package net.anawesomguy.snowiergolems.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static net.minecraft.client.renderer.block.model.ItemTransform.Deserializer.DEFAULT_ROTATION;
import static net.minecraft.client.renderer.block.model.ItemTransform.Deserializer.DEFAULT_SCALE;
import static net.minecraft.client.renderer.block.model.ItemTransform.Deserializer.DEFAULT_TRANSLATION;

@EventBusSubscriber(Dist.CLIENT)
public final class SnowierGolemsClient {
    public static final ContextKey<ItemStack> HAT_KEY = new ContextKey<>(SnowierGolems.GOLEM_HAT_ID);

    public static final List<Material> NORMAL_FACES = List.of(pumpkinFace("face_1"), pumpkinFace("face_2"),
                                                              pumpkinFace("face_3"), pumpkinFace("face_4"));
    public static final List<Material> ANGRY_FACES = List.of(pumpkinFace("angry_1"), pumpkinFace("angry_2"));
    public static final List<Material> LIT_FACES = List.of(
        blockMaterial(Identifier.withDefaultNamespace("block/jack_o_lantern")), pumpkinFace("three_eyed_lit"));
    public static final Material ONE_EYED_FACE = pumpkinFace("one_eyed");
    public static final Material THREE_EYED_FACE = pumpkinFace("three_eyed");
    public static final Material FROST_FACE = pumpkinFace("frost");
    public static final Material SNOWY_PUMPKIN_SIDE = blockMaterial(SnowierGolems.id("block/pumpkin/snowy_pumpkin"));
    public static final Material PUMPKIN_SIDE = blockMaterial(Identifier.withDefaultNamespace("block/pumpkin_side"));
    public static final Material SNOW = blockMaterial(Identifier.withDefaultNamespace("block/snow"));
    public static final Material PUMPKIN_TOP = blockMaterial(Identifier.withDefaultNamespace("block/pumpkin_top"));
    public static final Material PUMPKIN_FACE = blockMaterial(Identifier.withDefaultNamespace("block/carved_pumpkin"));

    public static final List<Material> ALL_FACES = ImmutableList.<Material>builder()
                                                                .addAll(NORMAL_FACES)
                                                                .addAll(ANGRY_FACES)
                                                                .addAll(LIT_FACES)
                                                                .add(ONE_EYED_FACE, THREE_EYED_FACE, FROST_FACE)
                                                                .build();

    @SuppressWarnings("unchecked") // list index is the faceId, and the map is from direction -> model
    public static final List<EnumMap<Direction, StandaloneModelKey<BlockModelPart>>> FACES_KEYS =
        Arrays.asList(new EnumMap[ALL_FACES.size()]);

    private static Material blockMaterial(Identifier texture) {
        return new Material(ModelManager.BLOCK_OR_ITEM, texture);
    }

    private static Material pumpkinFace(String faceName) {
        return blockMaterial(SnowierGolems.id("block/pumpkin/face/" + faceName));
    }

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GolemObjects.GOLEM_HAT_TYPE, GolemHatRenderer::new);
        event.registerEntityRenderer(GolemObjects.ENCHANTED_SNOWBALL, ThrownItemRenderer::new);
    }

    @SubscribeEvent
    private static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(SnowierGolems.GOLEM_HAT_ID, GolemHatSpecialRenderer.Unbaked.CODEC);
    }

    @SubscribeEvent
    private static void registerRenderStateModifiersEvent(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
            SnowGolemRenderer.class,
            (golem, state) -> state.setRenderData(HAT_KEY, golem.getItemBySlot(EquipmentSlot.HEAD)));
    }

    @SubscribeEvent
    private static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        for (int i = 0, size = ALL_FACES.size(); i < size; i++) {
            Material face = ALL_FACES.get(i);
            EnumMap<Direction, StandaloneModelKey<BlockModelPart>> map =
                new EnumMap<>(Direction.class);
            FACES_KEYS.set(i, map);
            for (Direction d : Direction.Plane.HORIZONTAL) {
                String name = SnowierGolems.MODID + ":" + // reversing `pumpkinFace`
                    ALL_FACES.get(i).texture().getPath().substring(19 /* "block/pumpkin/face/" */) + "_" + d;
                ModelDebugName debugName = () -> name;
                StandaloneModelKey<BlockModelPart> key = new StandaloneModelKey<>(debugName);
                map.put(d, key);
                event.register(key, new StandaloneGolemHatModel(d, face, debugName));
            }
        }
    }

    public record StandaloneGolemHatModel(Direction facing, Material face, ModelDebugName debugName)
        implements UnbakedStandaloneModel<BlockModelPart> {
        private static final Identifier CUBE = Identifier.withDefaultNamespace("block/cube");
        public static final ItemTransforms TRANSFORMS;

        static {
            // see models/block/golem_hat.json
            Vector3fc rot180 = new Vector3f(0F, 180F, 0F);
            ItemTransform thirdPerson = new ItemTransform(new Vector3f(75F, 45F, 0F), DEFAULT_TRANSLATION,
                                                          new Vector3f(0.375F), DEFAULT_ROTATION);
            ItemTransform firstPerson = new ItemTransform(new Vector3f(0F, 135F, 0F), DEFAULT_TRANSLATION,
                                                          new Vector3f(0.4F), DEFAULT_ROTATION);
            TRANSFORMS = new ItemTransforms(thirdPerson, thirdPerson, firstPerson, firstPerson,
                                            new ItemTransform(DEFAULT_ROTATION, DEFAULT_TRANSLATION, // head
                                                              DEFAULT_SCALE, DEFAULT_ROTATION),
                                            new ItemTransform(new Vector3f(30F, 225F, 0F), DEFAULT_TRANSLATION, // gui
                                                              new Vector3f(0.625F), DEFAULT_ROTATION),
                                            new ItemTransform(DEFAULT_ROTATION, new Vector3f(0F, 3F, 0F), // ground
                                                              new Vector3f(0.25F), DEFAULT_ROTATION),
                                            new ItemTransform(rot180, new Vector3f(0.5F), // fixed
                                                              DEFAULT_TRANSLATION, DEFAULT_ROTATION),
                                            ItemTransform.NO_TRANSFORM, ImmutableMap.of());
        }

        @Override
        public BlockModelPart bake(ModelBaker baker) {
            boolean frost = FROST_FACE.equals(face);
            TextureSlots.Data.Builder data = new TextureSlots.Data.Builder()
                .addTexture("particle", PUMPKIN_FACE)
                .addTexture("up", frost ? SNOW : PUMPKIN_TOP)
                .addTexture("down", PUMPKIN_TOP);

            Material side = frost ? SNOWY_PUMPKIN_SIDE : PUMPKIN_SIDE;
            for (Direction d : Direction.Plane.HORIZONTAL)
                data.addTexture(d.getName(), d == facing ? face : side);

            return SimpleModelWrapper.bake(baker, baker.resolveInlineModel(
                new BlockModel(null, null, null, TRANSFORMS, data.build(), CUBE),
                debugName
            ), BlockModelRotation.IDENTITY);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(CUBE);
        }
    }
}
