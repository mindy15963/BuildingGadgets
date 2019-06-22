package com.direwolf20.buildinggadgets.api;

import net.minecraft.util.ResourceLocation;

public final class APIReference {
    private APIReference() {}

    public static final String MODID = "buildinggadgets_api";

    public static final String MARKER_AFTER = MODID + ":after";
    public static final ResourceLocation MARKER_AFTER_RL = new ResourceLocation(MARKER_AFTER);
    public static final String MARKER_BEFORE = MODID + ":before";
    public static final ResourceLocation MARKER_BEFORE_RL = new ResourceLocation(MARKER_BEFORE);

    public static final class TileDataSerializerReference {
        public static final ResourceLocation REGISTRY_ID_TILE_DATA_SERIALIZER = new ResourceLocation(MODID, "tile_data/serializer");

        private TileDataSerializerReference() {}

        public static final String DUMMY_SERIALIZER = MODID + ":dummy_serializer";
        public static final ResourceLocation DUMMY_SERIALIZER_RL = new ResourceLocation(DUMMY_SERIALIZER);
    }

    public static final class TemplateSerializerReference {
        public static final ResourceLocation REGISTRY_ID_TEMPLATE_SERIALIZER = new ResourceLocation(MODID, "template/serializer");

        private TemplateSerializerReference() {}

    }

    public static final class TileDataFactoryReference {
        public static final String IMC_METHOD_TILEDATA_FACTORY = "imc_tile_data_factory";

        private TileDataFactoryReference() {}

        public static final String DATA_PROVIDER_FACTORY = MODID + ":data_provider_factory";
        public static final ResourceLocation DATA_PROVIDER_FACTORY_RL = new ResourceLocation(DATA_PROVIDER_FACTORY);
    }
}
