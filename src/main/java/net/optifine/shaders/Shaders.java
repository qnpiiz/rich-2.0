package net.optifine.shaders;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.GlErrors;
import net.optifine.Lang;
import net.optifine.config.ConnectedParser;
import net.optifine.expr.IExpressionBool;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.render.RenderTypes;
import net.optifine.render.RenderUtils;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import net.optifine.util.ArrayUtils;
import net.optifine.util.DynamicDimension;
import net.optifine.util.EntityUtils;
import net.optifine.util.LineBuffer;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import net.optifine.util.TimedEvent;
import net.optifine.util.WorldUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTGeometryShader4;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;

public class Shaders
{
    static Minecraft mc;
    static GameRenderer entityRenderer;
    public static boolean isInitializedOnce = false;
    public static boolean isShaderPackInitialized = false;
    public static GLCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap = false;
    public static int countResetDisplayLists = 0;
    private static int renderDisplayWidth = 0;
    private static int renderDisplayHeight = 0;
    public static int renderWidth = 0;
    public static int renderHeight = 0;
    public static boolean isRenderingWorld = false;
    public static boolean isRenderingSky = false;
    public static boolean isCompositeRendered = false;
    public static boolean isRenderingDfb = false;
    public static boolean isShadowPass = false;
    public static boolean isEntitiesGlowing = false;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    private static boolean isHandRenderedOff;
    private static boolean skipRenderHandMain;
    private static boolean skipRenderHandOff;
    public static boolean renderItemKeepDepthMask = false;
    public static boolean itemToRenderMainTranslucent = false;
    public static boolean itemToRenderOffTranslucent = false;
    static float[] sunPosition = new float[4];
    static float[] moonPosition = new float[4];
    static float[] shadowLightPosition = new float[4];
    static float[] upPosition = new float[4];
    static float[] shadowLightPositionVector = new float[4];
    static float[] upPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] sunPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] moonPosModelView = new float[] {0.0F, -100.0F, 0.0F, 0.0F};
    private static float[] tempMat = new float[16];
    static Vector4f clearColor = new Vector4f();
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime = 0L;
    static long lastWorldTime = 0L;
    static long diffWorldTime = 0L;
    static float celestialAngle = 0.0F;
    static float sunAngle = 0.0F;
    static float shadowAngle = 0.0F;
    static int moonPhase = 0;
    static long systemTime = 0L;
    static long lastSystemTime = 0L;
    static long diffSystemTime = 0L;
    static int frameCounter = 0;
    static float frameTime = 0.0F;
    static float frameTimeCounter = 0.0F;
    static int systemTimeInt32 = 0;
    public static PointOfView pointOfView = PointOfView.FIRST_PERSON;
    public static boolean pointOfViewChanged = false;
    static float rainStrength = 0.0F;
    static float wetness = 0.0F;
    public static float wetnessHalfLife = 600.0F;
    public static float drynessHalfLife = 200.0F;
    public static float eyeBrightnessHalflife = 10.0F;
    static boolean usewetness = false;
    static int isEyeInWater = 0;
    static int eyeBrightness = 0;
    static float eyeBrightnessFadeX = 0.0F;
    static float eyeBrightnessFadeY = 0.0F;
    static float eyePosY = 0.0F;
    static float centerDepth = 0.0F;
    static float centerDepthSmooth = 0.0F;
    static float centerDepthSmoothHalflife = 1.0F;
    static boolean centerDepthSmoothEnabled = false;
    static int superSamplingLevel = 1;
    static float nightVision = 0.0F;
    static float blindness = 0.0F;
    static boolean lightmapEnabled = false;
    static boolean fogEnabled = true;
    static RenderStage renderStage = RenderStage.NONE;
    private static int baseAttribId = 11;
    public static int entityAttrib = baseAttribId + 0;
    public static int midTexCoordAttrib = baseAttribId + 1;
    public static int tangentAttrib = baseAttribId + 2;
    public static int velocityAttrib = baseAttribId + 3;
    public static int midBlockAttrib = baseAttribId + 4;
    public static boolean useEntityAttrib = false;
    public static boolean useMidTexCoordAttrib = false;
    public static boolean useTangentAttrib = false;
    public static boolean useVelocityAttrib = false;
    public static boolean useMidBlockAttrib = false;
    public static boolean progUseEntityAttrib = false;
    public static boolean progUseMidTexCoordAttrib = false;
    public static boolean progUseTangentAttrib = false;
    public static boolean progUseVelocityAttrib = false;
    public static boolean progUseMidBlockAttrib = false;
    private static boolean progArbGeometryShader4 = false;
    private static boolean progExtGeometryShader4 = false;
    private static int progMaxVerticesOut = 3;
    private static boolean hasGeometryShaders = false;
    public static int atlasSizeX = 0;
    public static int atlasSizeY = 0;
    private static ShaderUniforms shaderUniforms = new ShaderUniforms();
    public static ShaderUniform4f uniform_entityColor = shaderUniforms.make4f("entityColor");
    public static ShaderUniform1i uniform_entityId = shaderUniforms.make1i("entityId");
    public static ShaderUniform1i uniform_blockEntityId = shaderUniforms.make1i("blockEntityId");
    public static ShaderUniform1i uniform_texture = shaderUniforms.make1i("texture");
    public static ShaderUniform1i uniform_lightmap = shaderUniforms.make1i("lightmap");
    public static ShaderUniform1i uniform_normals = shaderUniforms.make1i("normals");
    public static ShaderUniform1i uniform_specular = shaderUniforms.make1i("specular");
    public static ShaderUniform1i uniform_shadow = shaderUniforms.make1i("shadow");
    public static ShaderUniform1i uniform_watershadow = shaderUniforms.make1i("watershadow");
    public static ShaderUniform1i uniform_shadowtex0 = shaderUniforms.make1i("shadowtex0");
    public static ShaderUniform1i uniform_shadowtex1 = shaderUniforms.make1i("shadowtex1");
    public static ShaderUniform1i uniform_depthtex0 = shaderUniforms.make1i("depthtex0");
    public static ShaderUniform1i uniform_depthtex1 = shaderUniforms.make1i("depthtex1");
    public static ShaderUniform1i uniform_shadowcolor = shaderUniforms.make1i("shadowcolor");
    public static ShaderUniform1i uniform_shadowcolor0 = shaderUniforms.make1i("shadowcolor0");
    public static ShaderUniform1i uniform_shadowcolor1 = shaderUniforms.make1i("shadowcolor1");
    public static ShaderUniform1i uniform_noisetex = shaderUniforms.make1i("noisetex");
    public static ShaderUniform1i uniform_gcolor = shaderUniforms.make1i("gcolor");
    public static ShaderUniform1i uniform_gdepth = shaderUniforms.make1i("gdepth");
    public static ShaderUniform1i uniform_gnormal = shaderUniforms.make1i("gnormal");
    public static ShaderUniform1i uniform_composite = shaderUniforms.make1i("composite");
    public static ShaderUniform1i uniform_gaux1 = shaderUniforms.make1i("gaux1");
    public static ShaderUniform1i uniform_gaux2 = shaderUniforms.make1i("gaux2");
    public static ShaderUniform1i uniform_gaux3 = shaderUniforms.make1i("gaux3");
    public static ShaderUniform1i uniform_gaux4 = shaderUniforms.make1i("gaux4");
    public static ShaderUniform1i uniform_colortex0 = shaderUniforms.make1i("colortex0");
    public static ShaderUniform1i uniform_colortex1 = shaderUniforms.make1i("colortex1");
    public static ShaderUniform1i uniform_colortex2 = shaderUniforms.make1i("colortex2");
    public static ShaderUniform1i uniform_colortex3 = shaderUniforms.make1i("colortex3");
    public static ShaderUniform1i uniform_colortex4 = shaderUniforms.make1i("colortex4");
    public static ShaderUniform1i uniform_colortex5 = shaderUniforms.make1i("colortex5");
    public static ShaderUniform1i uniform_colortex6 = shaderUniforms.make1i("colortex6");
    public static ShaderUniform1i uniform_colortex7 = shaderUniforms.make1i("colortex7");
    public static ShaderUniform1i uniform_gdepthtex = shaderUniforms.make1i("gdepthtex");
    public static ShaderUniform1i uniform_depthtex2 = shaderUniforms.make1i("depthtex2");
    public static ShaderUniform1i uniform_colortex8 = shaderUniforms.make1i("colortex8");
    public static ShaderUniform1i uniform_colortex9 = shaderUniforms.make1i("colortex9");
    public static ShaderUniform1i uniform_colortex10 = shaderUniforms.make1i("colortex10");
    public static ShaderUniform1i uniform_colortex11 = shaderUniforms.make1i("colortex11");
    public static ShaderUniform1i uniform_colortex12 = shaderUniforms.make1i("colortex12");
    public static ShaderUniform1i uniform_colortex13 = shaderUniforms.make1i("colortex13");
    public static ShaderUniform1i uniform_colortex14 = shaderUniforms.make1i("colortex14");
    public static ShaderUniform1i uniform_colortex15 = shaderUniforms.make1i("colortex15");
    public static ShaderUniform1i uniform_colorimg0 = shaderUniforms.make1i("colorimg0");
    public static ShaderUniform1i uniform_colorimg1 = shaderUniforms.make1i("colorimg1");
    public static ShaderUniform1i uniform_colorimg2 = shaderUniforms.make1i("colorimg2");
    public static ShaderUniform1i uniform_colorimg3 = shaderUniforms.make1i("colorimg3");
    public static ShaderUniform1i uniform_colorimg4 = shaderUniforms.make1i("colorimg4");
    public static ShaderUniform1i uniform_colorimg5 = shaderUniforms.make1i("colorimg5");
    public static ShaderUniform1i uniform_shadowcolorimg0 = shaderUniforms.make1i("shadowcolorimg0");
    public static ShaderUniform1i uniform_shadowcolorimg1 = shaderUniforms.make1i("shadowcolorimg1");
    public static ShaderUniform1i uniform_tex = shaderUniforms.make1i("tex");
    public static ShaderUniform1i uniform_heldItemId = shaderUniforms.make1i("heldItemId");
    public static ShaderUniform1i uniform_heldBlockLightValue = shaderUniforms.make1i("heldBlockLightValue");
    public static ShaderUniform1i uniform_heldItemId2 = shaderUniforms.make1i("heldItemId2");
    public static ShaderUniform1i uniform_heldBlockLightValue2 = shaderUniforms.make1i("heldBlockLightValue2");
    public static ShaderUniform1i uniform_fogMode = shaderUniforms.make1i("fogMode");
    public static ShaderUniform1f uniform_fogDensity = shaderUniforms.make1f("fogDensity");
    public static ShaderUniform3f uniform_fogColor = shaderUniforms.make3f("fogColor");
    public static ShaderUniform3f uniform_skyColor = shaderUniforms.make3f("skyColor");
    public static ShaderUniform1i uniform_worldTime = shaderUniforms.make1i("worldTime");
    public static ShaderUniform1i uniform_worldDay = shaderUniforms.make1i("worldDay");
    public static ShaderUniform1i uniform_moonPhase = shaderUniforms.make1i("moonPhase");
    public static ShaderUniform1i uniform_frameCounter = shaderUniforms.make1i("frameCounter");
    public static ShaderUniform1f uniform_frameTime = shaderUniforms.make1f("frameTime");
    public static ShaderUniform1f uniform_frameTimeCounter = shaderUniforms.make1f("frameTimeCounter");
    public static ShaderUniform1f uniform_sunAngle = shaderUniforms.make1f("sunAngle");
    public static ShaderUniform1f uniform_shadowAngle = shaderUniforms.make1f("shadowAngle");
    public static ShaderUniform1f uniform_rainStrength = shaderUniforms.make1f("rainStrength");
    public static ShaderUniform1f uniform_aspectRatio = shaderUniforms.make1f("aspectRatio");
    public static ShaderUniform1f uniform_viewWidth = shaderUniforms.make1f("viewWidth");
    public static ShaderUniform1f uniform_viewHeight = shaderUniforms.make1f("viewHeight");
    public static ShaderUniform1f uniform_near = shaderUniforms.make1f("near");
    public static ShaderUniform1f uniform_far = shaderUniforms.make1f("far");
    public static ShaderUniform3f uniform_sunPosition = shaderUniforms.make3f("sunPosition");
    public static ShaderUniform3f uniform_moonPosition = shaderUniforms.make3f("moonPosition");
    public static ShaderUniform3f uniform_shadowLightPosition = shaderUniforms.make3f("shadowLightPosition");
    public static ShaderUniform3f uniform_upPosition = shaderUniforms.make3f("upPosition");
    public static ShaderUniform3f uniform_previousCameraPosition = shaderUniforms.make3f("previousCameraPosition");
    public static ShaderUniform3f uniform_cameraPosition = shaderUniforms.make3f("cameraPosition");
    public static ShaderUniformM4 uniform_gbufferModelView = shaderUniforms.makeM4("gbufferModelView");
    public static ShaderUniformM4 uniform_gbufferModelViewInverse = shaderUniforms.makeM4("gbufferModelViewInverse");
    public static ShaderUniformM4 uniform_gbufferPreviousProjection = shaderUniforms.makeM4("gbufferPreviousProjection");
    public static ShaderUniformM4 uniform_gbufferProjection = shaderUniforms.makeM4("gbufferProjection");
    public static ShaderUniformM4 uniform_gbufferProjectionInverse = shaderUniforms.makeM4("gbufferProjectionInverse");
    public static ShaderUniformM4 uniform_gbufferPreviousModelView = shaderUniforms.makeM4("gbufferPreviousModelView");
    public static ShaderUniformM4 uniform_shadowProjection = shaderUniforms.makeM4("shadowProjection");
    public static ShaderUniformM4 uniform_shadowProjectionInverse = shaderUniforms.makeM4("shadowProjectionInverse");
    public static ShaderUniformM4 uniform_shadowModelView = shaderUniforms.makeM4("shadowModelView");
    public static ShaderUniformM4 uniform_shadowModelViewInverse = shaderUniforms.makeM4("shadowModelViewInverse");
    public static ShaderUniform1f uniform_wetness = shaderUniforms.make1f("wetness");
    public static ShaderUniform1f uniform_eyeAltitude = shaderUniforms.make1f("eyeAltitude");
    public static ShaderUniform2i uniform_eyeBrightness = shaderUniforms.make2i("eyeBrightness");
    public static ShaderUniform2i uniform_eyeBrightnessSmooth = shaderUniforms.make2i("eyeBrightnessSmooth");
    public static ShaderUniform2i uniform_terrainTextureSize = shaderUniforms.make2i("terrainTextureSize");
    public static ShaderUniform1i uniform_terrainIconSize = shaderUniforms.make1i("terrainIconSize");
    public static ShaderUniform1i uniform_isEyeInWater = shaderUniforms.make1i("isEyeInWater");
    public static ShaderUniform1f uniform_nightVision = shaderUniforms.make1f("nightVision");
    public static ShaderUniform1f uniform_blindness = shaderUniforms.make1f("blindness");
    public static ShaderUniform1f uniform_screenBrightness = shaderUniforms.make1f("screenBrightness");
    public static ShaderUniform1i uniform_hideGUI = shaderUniforms.make1i("hideGUI");
    public static ShaderUniform1f uniform_centerDepthSmooth = shaderUniforms.make1f("centerDepthSmooth");
    public static ShaderUniform2i uniform_atlasSize = shaderUniforms.make2i("atlasSize");
    public static ShaderUniform4f uniform_spriteBounds = shaderUniforms.make4f("spriteBounds");
    public static ShaderUniform4i uniform_blendFunc = shaderUniforms.make4i("blendFunc");
    public static ShaderUniform1i uniform_instanceId = shaderUniforms.make1i("instanceId");
    public static ShaderUniform1f uniform_playerMood = shaderUniforms.make1f("playerMood");
    public static ShaderUniform1i uniform_renderStage = shaderUniforms.make1i("renderStage");
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int cameraOffsetX;
    static int cameraOffsetZ;
    static boolean hasShadowMap = false;
    public static boolean needResizeShadow = false;
    static int shadowMapWidth = 1024;
    static int shadowMapHeight = 1024;
    static int spShadowMapWidth = 1024;
    static int spShadowMapHeight = 1024;
    static float shadowMapFOV = 90.0F;
    static float shadowMapHalfPlane = 160.0F;
    static boolean shadowMapIsOrtho = true;
    static float shadowDistanceRenderMul = -1.0F;
    public static boolean shouldSkipDefaultShadow = false;
    static boolean waterShadowEnabled = false;
    public static final int MaxDrawBuffers = 8;
    public static final int MaxColorBuffers = 16;
    public static final int MaxDepthBuffers = 3;
    public static final int MaxShadowColorBuffers = 2;
    public static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers = 0;
    static int usedDepthBuffers = 0;
    static int usedShadowColorBuffers = 0;
    static int usedShadowDepthBuffers = 0;
    static int usedColorAttachs = 0;
    static int usedDrawBuffers = 0;
    static boolean bindImageTextures = false;
    static ShadersFramebuffer dfb;
    static ShadersFramebuffer sfb;
    private static int[] gbuffersFormat = new int[16];
    public static boolean[] gbuffersClear = new boolean[16];
    public static Vector4f[] gbuffersClearColor = new Vector4f[16];
    private static final Vector4f CLEAR_COLOR_0 = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
    private static final Vector4f CLEAR_COLOR_1 = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    private static int[] shadowBuffersFormat = new int[2];
    public static boolean[] shadowBuffersClear = new boolean[2];
    public static Vector4f[] shadowBuffersClearColor = new Vector4f[2];
    private static Programs programs = new Programs();
    public static final Program ProgramNone = programs.getProgramNone();
    public static final Program ProgramShadow = programs.makeShadow("shadow", ProgramNone);
    public static final Program ProgramShadowSolid = programs.makeShadow("shadow_solid", ProgramShadow);
    public static final Program ProgramShadowCutout = programs.makeShadow("shadow_cutout", ProgramShadow);
    public static final Program[] ProgramsShadowcomp = programs.makeShadowcomps("shadowcomp", 16);
    public static final Program[] ProgramsPrepare = programs.makePrepares("prepare", 16);
    public static final Program ProgramBasic = programs.makeGbuffers("gbuffers_basic", ProgramNone);
    public static final Program ProgramTextured = programs.makeGbuffers("gbuffers_textured", ProgramBasic);
    public static final Program ProgramTexturedLit = programs.makeGbuffers("gbuffers_textured_lit", ProgramTextured);
    public static final Program ProgramSkyBasic = programs.makeGbuffers("gbuffers_skybasic", ProgramBasic);
    public static final Program ProgramSkyTextured = programs.makeGbuffers("gbuffers_skytextured", ProgramTextured);
    public static final Program ProgramClouds = programs.makeGbuffers("gbuffers_clouds", ProgramTextured);
    public static final Program ProgramTerrain = programs.makeGbuffers("gbuffers_terrain", ProgramTexturedLit);
    public static final Program ProgramTerrainSolid = programs.makeGbuffers("gbuffers_terrain_solid", ProgramTerrain);
    public static final Program ProgramTerrainCutoutMip = programs.makeGbuffers("gbuffers_terrain_cutout_mip", ProgramTerrain);
    public static final Program ProgramTerrainCutout = programs.makeGbuffers("gbuffers_terrain_cutout", ProgramTerrain);
    public static final Program ProgramDamagedBlock = programs.makeGbuffers("gbuffers_damagedblock", ProgramTerrain);
    public static final Program ProgramBlock = programs.makeGbuffers("gbuffers_block", ProgramTerrain);
    public static final Program ProgramBeaconBeam = programs.makeGbuffers("gbuffers_beaconbeam", ProgramTextured);
    public static final Program ProgramItem = programs.makeGbuffers("gbuffers_item", ProgramTexturedLit);
    public static final Program ProgramEntities = programs.makeGbuffers("gbuffers_entities", ProgramTexturedLit);
    public static final Program ProgramEntitiesGlowing = programs.makeGbuffers("gbuffers_entities_glowing", ProgramEntities);
    public static final Program ProgramArmorGlint = programs.makeGbuffers("gbuffers_armor_glint", ProgramTextured);
    public static final Program ProgramSpiderEyes = programs.makeGbuffers("gbuffers_spidereyes", ProgramTextured);
    public static final Program ProgramHand = programs.makeGbuffers("gbuffers_hand", ProgramTexturedLit);
    public static final Program ProgramWeather = programs.makeGbuffers("gbuffers_weather", ProgramTexturedLit);
    public static final Program ProgramDeferredPre = programs.makeVirtual("deferred_pre");
    public static final Program[] ProgramsDeferred = programs.makeDeferreds("deferred", 16);
    public static final Program ProgramDeferred = ProgramsDeferred[0];
    public static final Program ProgramWater = programs.makeGbuffers("gbuffers_water", ProgramTerrain);
    public static final Program ProgramHandWater = programs.makeGbuffers("gbuffers_hand_water", ProgramHand);
    public static final Program ProgramCompositePre = programs.makeVirtual("composite_pre");
    public static final Program[] ProgramsComposite = programs.makeComposites("composite", 16);
    public static final Program ProgramComposite = ProgramsComposite[0];
    public static final Program ProgramFinal = programs.makeComposite("final");
    public static final int ProgramCount = programs.getCount();
    public static final Program[] ProgramsAll = programs.getPrograms();
    public static Program activeProgram = ProgramNone;
    public static int activeProgramID = 0;
    private static ProgramStack programStack = new ProgramStack();
    private static boolean hasDeferredPrograms = false;
    public static boolean hasShadowcompPrograms = false;
    public static boolean hasPreparePrograms = false;
    public static Properties loadedShaders = null;
    public static Properties shadersConfig = null;
    public static Texture defaultTexture = null;
    public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
    public static boolean[] shadowMipmapEnabled = new boolean[2];
    public static boolean[] shadowFilterNearest = new boolean[2];
    public static boolean[] shadowColorMipmapEnabled = new boolean[2];
    public static boolean[] shadowColorFilterNearest = new boolean[2];
    public static boolean configTweakBlockDamage = false;
    public static boolean configCloudShadow = false;
    public static float configHandDepthMul = 0.125F;
    public static float configRenderResMul = 1.0F;
    public static float configShadowResMul = 1.0F;
    public static int configTexMinFilB = 0;
    public static int configTexMinFilN = 0;
    public static int configTexMinFilS = 0;
    public static int configTexMagFilB = 0;
    public static int configTexMagFilN = 0;
    public static int configTexMagFilS = 0;
    public static boolean configShadowClipFrustrum = true;
    public static boolean configNormalMap = true;
    public static boolean configSpecularMap = true;
    public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static int configAntialiasingLevel = 0;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc = new String[] {"Nearest", "Nearest-Nearest", "Nearest-Linear"};
    public static final String[] texMagFilDesc = new String[] {"Nearest", "Linear"};
    public static final int[] texMinFilValue = new int[] {9728, 9984, 9986};
    public static final int[] texMagFilValue = new int[] {9728, 9729};
    private static IShaderPack shaderPack = null;
    public static boolean shaderPackLoaded = false;
    public static String currentShaderName;
    public static final String SHADER_PACK_NAME_NONE = "OFF";
    public static final String SHADER_PACK_NAME_DEFAULT = "(internal)";
    public static final String SHADER_PACKS_DIR_NAME = "shaderpacks";
    public static final String OPTIONS_FILE_NAME = "optionsshaders.txt";
    public static final File shaderPacksDir = new File(Minecraft.getInstance().gameDir, "shaderpacks");
    static File configFile = new File(Minecraft.getInstance().gameDir, "optionsshaders.txt");
    private static ShaderOption[] shaderPackOptions = null;
    private static Set<String> shaderPackOptionSliders = null;
    static ShaderProfile[] shaderPackProfiles = null;
    static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
    static Map<String, IExpressionBool> shaderPackProgramConditions = new HashMap<>();
    public static final String PATH_SHADERS_PROPERTIES = "/shaders/shaders.properties";
    public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
    public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowTerrain = new PropertyDefaultTrueFalse("shadowTerrain", "Shadow Terrain", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowEntities = new PropertyDefaultTrueFalse("shadowEntities", "Shadow Entities", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowBlockEntities = new PropertyDefaultTrueFalse("shadowBlockEntities", "Shadow Block Entities", 0);
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
    public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
    public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
    public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth", 0);
    public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth", 0);
    public static PropertyDefaultTrueFalse shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO", 0);
    public static PropertyDefaultTrueFalse shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling", 0);
    private static Map<String, String> shaderPackResources = new HashMap<>();
    private static ClientWorld currentWorld = null;
    private static List<Integer> shaderPackDimensions = new ArrayList<>();
    private static ICustomTexture[] customTexturesGbuffers = null;
    private static ICustomTexture[] customTexturesComposite = null;
    private static ICustomTexture[] customTexturesDeferred = null;
    private static ICustomTexture[] customTexturesShadowcomp = null;
    private static ICustomTexture[] customTexturesPrepare = null;
    private static String noiseTexturePath = null;
    private static DynamicDimension[] colorBufferSizes = new DynamicDimension[16];
    private static CustomUniforms customUniforms = null;
    public static final boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
    public static float blockLightLevel05 = 0.5F;
    public static float blockLightLevel06 = 0.6F;
    public static float blockLightLevel08 = 0.8F;
    public static float aoLevel = -1.0F;
    public static float sunPathRotation = 0.0F;
    public static float shadowAngleInterval = 0.0F;
    public static int fogMode = 0;
    public static float fogDensity = 0.0F;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize = 2.0F;
    public static int terrainIconSize = 16;
    public static int[] terrainTextureSize = new int[2];
    private static ICustomTexture noiseTexture;
    private static boolean noiseTextureEnabled = false;
    private static int noiseTextureResolution = 256;
    static final int[] colorTextureImageUnit = new int[] {0, 1, 2, 3, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23};
    static final int[] depthTextureImageUnit = new int[] {6, 11, 12};
    static final int[] shadowColorTextureImageUnit = new int[] {13, 14};
    static final int[] shadowDepthTextureImageUnit = new int[] {4, 5};
    static final int[] colorImageUnit = new int[] {0, 1, 2, 3, 4, 5};
    static final int[] shadowColorImageUnit = new int[] {6, 7};
    private static final int bigBufferSize = (295 + 8 * ProgramCount) * 4;
    private static final ByteBuffer bigBuffer = (ByteBuffer)((Buffer)BufferUtils.createByteBuffer(bigBufferSize)).limit(0);
    static final float[] faProjection = new float[16];
    static final float[] faProjectionInverse = new float[16];
    static final float[] faModelView = new float[16];
    static final float[] faModelViewInverse = new float[16];
    static final float[] faShadowProjection = new float[16];
    static final float[] faShadowProjectionInverse = new float[16];
    static final float[] faShadowModelView = new float[16];
    static final float[] faShadowModelViewInverse = new float[16];
    static final FloatBuffer projection = nextFloatBuffer(16);
    static final FloatBuffer projectionInverse = nextFloatBuffer(16);
    static final FloatBuffer modelView = nextFloatBuffer(16);
    static final FloatBuffer modelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowProjection = nextFloatBuffer(16);
    static final FloatBuffer shadowProjectionInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowModelView = nextFloatBuffer(16);
    static final FloatBuffer shadowModelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer previousProjection = nextFloatBuffer(16);
    static final FloatBuffer previousModelView = nextFloatBuffer(16);
    static final FloatBuffer tempMatrixDirectBuffer = nextFloatBuffer(16);
    static final FloatBuffer tempDirectFloatBuffer = nextFloatBuffer(16);
    static final DrawBuffers dfbDrawBuffers = new DrawBuffers("dfbDrawBuffers", 16, 8);
    static final DrawBuffers sfbDrawBuffers = new DrawBuffers("sfbDrawBuffers", 16, 8);
    static final DrawBuffers drawBuffersNone = (new DrawBuffers("drawBuffersNone", 16, 8)).limit(0);
    static final DrawBuffers[] drawBuffersColorAtt = makeDrawBuffersColorSingle(16);
    static boolean glDebugGroups;
    static boolean glDebugGroupProgram;
    static Map<Block, Integer> mapBlockToEntityData;
    private static final String[] formatNames = new String[] {"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R8I", "RG8I", "RGB8I", "RGBA8I", "R8UI", "RG8UI", "RGB8UI", "RGBA8UI", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R16I", "RG16I", "RGB16I", "RGBA16I", "R16UI", "RG16UI", "RGB16UI", "RGBA16UI", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5"};
    private static final int[] formatIds = new int[] {33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33329, 33335, 36239, 36238, 33330, 33336, 36221, 36220, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33331, 33337, 36233, 36232, 33332, 33338, 36215, 36214, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901};
    private static final Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
    public static int[] entityData = new int[32];
    public static int entityDataIndex = 0;

    private Shaders()
    {
    }

    private static ByteBuffer nextByteBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        ((Buffer)bytebuffer).position(i).limit(i + size);
        return bytebuffer.slice();
    }

    public static IntBuffer nextIntBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        ((Buffer)bytebuffer).position(i).limit(i + size * 4);
        return bytebuffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        ((Buffer)bytebuffer).position(i).limit(i + size * 4);
        return bytebuffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size)
    {
        IntBuffer[] aintbuffer = new IntBuffer[count];

        for (int i = 0; i < count; ++i)
        {
            aintbuffer[i] = nextIntBuffer(size);
        }

        return aintbuffer;
    }

    private static DrawBuffers[] makeDrawBuffersColorSingle(int count)
    {
        DrawBuffers[] adrawbuffers = new DrawBuffers[count];

        for (int i = 0; i < adrawbuffers.length; ++i)
        {
            DrawBuffers drawbuffers = new DrawBuffers("single" + i, 16, 8);
            drawbuffers.put(36064 + i);
            drawbuffers.position(0);
            drawbuffers.limit(1);
            adrawbuffers[i] = drawbuffers;
        }

        return adrawbuffers;
    }

    public static void loadConfig()
    {
        SMCLog.info("Load shaders configuration.");

        try
        {
            if (!shaderPacksDir.exists())
            {
                shaderPacksDir.mkdir();
            }
        }
        catch (Exception exception2)
        {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderPacksDir);
        }

        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");

        if (configFile.exists())
        {
            try
            {
                FileReader filereader = new FileReader(configFile);
                shadersConfig.load(filereader);
                filereader.close();
            }
            catch (Exception exception1)
            {
            }
        }

        if (!configFile.exists())
        {
            try
            {
                storeConfig();
            }
            catch (Exception exception)
            {
            }
        }

        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();

        for (int i = 0; i < aenumshaderoption.length; ++i)
        {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = enumshaderoption.getValueDefault();
            String s2 = shadersConfig.getProperty(s, s1);
            setEnumShaderOption(enumshaderoption, s2);
        }

        loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str)
    {
        if (str == null)
        {
            str = eso.getValueDefault();
        }

        switch (eso)
        {
            case ANTIALIASING:
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;

            case NORMAL_MAP:
                configNormalMap = Config.parseBoolean(str, true);
                break;

            case SPECULAR_MAP:
                configSpecularMap = Config.parseBoolean(str, true);
                break;

            case RENDER_RES_MUL:
                configRenderResMul = Config.parseFloat(str, 1.0F);
                break;

            case SHADOW_RES_MUL:
                configShadowResMul = Config.parseFloat(str, 1.0F);
                break;

            case HAND_DEPTH_MUL:
                configHandDepthMul = Config.parseFloat(str, 0.125F);
                break;

            case CLOUD_SHADOW:
                configCloudShadow = Config.parseBoolean(str, true);
                break;

            case OLD_HAND_LIGHT:
                configOldHandLight.setPropertyValue(str);
                break;

            case OLD_LIGHTING:
                configOldLighting.setPropertyValue(str);
                break;

            case SHADER_PACK:
                currentShaderName = str;
                break;

            case TWEAK_BLOCK_DAMAGE:
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;

            case SHADOW_CLIP_FRUSTRUM:
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;

            case TEX_MIN_FIL_B:
                configTexMinFilB = Config.parseInt(str, 0);
                break;

            case TEX_MIN_FIL_N:
                configTexMinFilN = Config.parseInt(str, 0);
                break;

            case TEX_MIN_FIL_S:
                configTexMinFilS = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_B:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_N:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_S:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void storeConfig()
    {
        SMCLog.info("Save shaders configuration.");

        if (shadersConfig == null)
        {
            shadersConfig = new PropertiesOrdered();
        }

        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();

        for (int i = 0; i < aenumshaderoption.length; ++i)
        {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = getEnumShaderOption(enumshaderoption);
            shadersConfig.setProperty(s, s1);
        }

        try
        {
            FileWriter filewriter = new FileWriter(configFile);
            shadersConfig.store(filewriter, (String)null);
            filewriter.close();
        }
        catch (Exception exception)
        {
            SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso)
    {
        switch (eso)
        {
            case ANTIALIASING:
                return Integer.toString(configAntialiasingLevel);

            case NORMAL_MAP:
                return Boolean.toString(configNormalMap);

            case SPECULAR_MAP:
                return Boolean.toString(configSpecularMap);

            case RENDER_RES_MUL:
                return Float.toString(configRenderResMul);

            case SHADOW_RES_MUL:
                return Float.toString(configShadowResMul);

            case HAND_DEPTH_MUL:
                return Float.toString(configHandDepthMul);

            case CLOUD_SHADOW:
                return Boolean.toString(configCloudShadow);

            case OLD_HAND_LIGHT:
                return configOldHandLight.getPropertyValue();

            case OLD_LIGHTING:
                return configOldLighting.getPropertyValue();

            case SHADER_PACK:
                return currentShaderName;

            case TWEAK_BLOCK_DAMAGE:
                return Boolean.toString(configTweakBlockDamage);

            case SHADOW_CLIP_FRUSTRUM:
                return Boolean.toString(configShadowClipFrustrum);

            case TEX_MIN_FIL_B:
                return Integer.toString(configTexMinFilB);

            case TEX_MIN_FIL_N:
                return Integer.toString(configTexMinFilN);

            case TEX_MIN_FIL_S:
                return Integer.toString(configTexMinFilS);

            case TEX_MAG_FIL_B:
                return Integer.toString(configTexMagFilB);

            case TEX_MAG_FIL_N:
                return Integer.toString(configTexMagFilB);

            case TEX_MAG_FIL_S:
                return Integer.toString(configTexMagFilB);

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void setShaderPack(String par1name)
    {
        currentShaderName = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        loadShaderPack();
    }

    public static void loadShaderPack()
    {
    	mc = Minecraft.getInstance();
        boolean flag = shaderPackLoaded;
        boolean flag1 = isOldLighting();

        if (mc.worldRenderer != null)
        {
            mc.worldRenderer.pauseChunkUpdates();
        }

        shaderPackLoaded = false;

        if (shaderPack != null)
        {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackProgramConditions.clear();
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            resetCustomTextures();
            noiseTexturePath = null;
        }

        boolean flag2 = false;

        if (Config.isAntialiasing())
        {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag2 = true;
        }

        if (Config.isGraphicsFabulous())
        {
            SMCLog.info("Shaders can not be loaded, Fabulous Graphics is enabled.");
            flag2 = true;
        }

        String s = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "(internal)");

        if (!flag2)
        {
            shaderPack = getShaderPack(s);
            shaderPackLoaded = shaderPack != null;
        }

        if (shaderPackLoaded)
        {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        }
        else
        {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }

        if (saveFinalShaders)
        {
            clearDirectory(new File(shaderPacksDir, "debug"));
        }

        loadShaderPackResources();
        loadShaderPackDimensions();
        shaderPackOptions = loadShaderPackOptions();
        loadShaderPackFixedProperties();
        loadShaderPackDynamicProperties();
        boolean flag3 = shaderPackLoaded != flag;
        boolean flag4 = isOldLighting() != flag1;

        if (flag3 || flag4)
        {
            DefaultVertexFormats.updateVertexFormats();

            if (Reflector.LightUtil.exists())
            {
                Reflector.LightUtil_itemConsumer.setValue((Object)null);
                Reflector.LightUtil_tessellator.setValue((Object)null);
            }

            updateBlockLightLevel();
        }

        if (mc.getResourceManager() != null)
        {
            CustomBlockLayers.update();
        }

        if (mc.worldRenderer != null)
        {
            mc.worldRenderer.resumeChunkUpdates();
        }

        if ((flag3 || flag4) && mc.getResourceManager() != null)
        {
            mc.scheduleResourcesRefresh();
        }
    }

    public static IShaderPack getShaderPack(String name)
    {
        if (name == null)
        {
            return null;
        }
        else
        {
            name = name.trim();

            if (!name.isEmpty() && !name.equals("OFF"))
            {
                if (name.equals("(internal)"))
                {
                    return new ShaderPackDefault();
                }
                else
                {
                    try
                    {
                        File file1 = new File(shaderPacksDir, name);

                        if (file1.isDirectory())
                        {
                            return new ShaderPackFolder(name, file1);
                        }
                        else
                        {
                            return file1.isFile() && name.toLowerCase().endsWith(".zip") ? new ShaderPackZip(name, file1) : null;
                        }
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                        return null;
                    }
                }
            }
            else
            {
                return null;
            }
        }
    }

    public static IShaderPack getShaderPack()
    {
        return shaderPack;
    }

    private static void loadShaderPackDimensions()
    {
        shaderPackDimensions.clear();

        for (int i = -128; i <= 128; ++i)
        {
            String s = "/shaders/world" + i;

            if (shaderPack.hasDirectory(s))
            {
                shaderPackDimensions.add(i);
            }
        }

        if (shaderPackDimensions.size() > 0)
        {
            Integer[] ainteger = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString((Object[])ainteger));
        }
    }

    private static void loadShaderPackFixedProperties()
    {
        shaderPackOldLighting.resetValue();
        shaderPackSeparateAo.resetValue();

        if (shaderPack != null)
        {
            String s = "/shaders/shaders.properties";

            try
            {
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream == null)
                {
                    return;
                }

                inputstream = MacroProcessor.process(inputstream, s, false);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackOldLighting.loadFrom(properties);
                shaderPackSeparateAo.loadFrom(properties);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(properties, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(properties, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(properties, shaderPackProfiles, shaderPackOptions);
            }
            catch (IOException ioexception)
            {
                Config.warn("[Shaders] Error reading: " + s);
            }
        }
    }

    private static void loadShaderPackDynamicProperties()
    {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackShadowTerrain.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackShadowEntities.resetValue();
        shaderPackShadowBlockEntities.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        shaderPackRainDepth.resetValue();
        shaderPackBeaconBeamDepth.resetValue();
        shaderPackFrustumCulling.resetValue();
        BlockAliases.reset();
        ItemAliases.reset();
        EntityAliases.reset();
        customUniforms = null;

        for (int i = 0; i < ProgramsAll.length; ++i)
        {
            Program program = ProgramsAll[i];
            program.resetProperties();
        }

        Arrays.fill(colorBufferSizes, (Object)null);

        if (shaderPack != null)
        {
            BlockAliases.update(shaderPack);
            ItemAliases.update(shaderPack);
            EntityAliases.update(shaderPack);
            String s = "/shaders/shaders.properties";

            try
            {
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream == null)
                {
                    return;
                }

                inputstream = MacroProcessor.process(inputstream, s, true);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackClouds.loadFrom(properties);
                shaderPackOldHandLight.loadFrom(properties);
                shaderPackDynamicHandLight.loadFrom(properties);
                shaderPackShadowTerrain.loadFrom(properties);
                shaderPackShadowTranslucent.loadFrom(properties);
                shaderPackShadowEntities.loadFrom(properties);
                shaderPackShadowBlockEntities.loadFrom(properties);
                shaderPackUnderwaterOverlay.loadFrom(properties);
                shaderPackSun.loadFrom(properties);
                shaderPackVignette.loadFrom(properties);
                shaderPackMoon.loadFrom(properties);
                shaderPackBackFaceSolid.loadFrom(properties);
                shaderPackBackFaceCutout.loadFrom(properties);
                shaderPackBackFaceCutoutMipped.loadFrom(properties);
                shaderPackBackFaceTranslucent.loadFrom(properties);
                shaderPackRainDepth.loadFrom(properties);
                shaderPackBeaconBeamDepth.loadFrom(properties);
                shaderPackFrustumCulling.loadFrom(properties);
                shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(properties, shaderPackOptions);
                customTexturesGbuffers = loadCustomTextures(properties, ProgramStage.GBUFFERS);
                customTexturesComposite = loadCustomTextures(properties, ProgramStage.COMPOSITE);
                customTexturesDeferred = loadCustomTextures(properties, ProgramStage.DEFERRED);
                customTexturesShadowcomp = loadCustomTextures(properties, ProgramStage.SHADOWCOMP);
                customTexturesPrepare = loadCustomTextures(properties, ProgramStage.PREPARE);
                noiseTexturePath = properties.getProperty("texture.noise");

                if (noiseTexturePath != null)
                {
                    noiseTextureEnabled = true;
                }

                customUniforms = ShaderPackParser.parseCustomUniforms(properties);
                ShaderPackParser.parseAlphaStates(properties);
                ShaderPackParser.parseBlendStates(properties);
                ShaderPackParser.parseRenderScales(properties);
                ShaderPackParser.parseBuffersFlip(properties);
                colorBufferSizes = ShaderPackParser.parseBufferSizes(properties, 16);
            }
            catch (IOException ioexception)
            {
                Config.warn("[Shaders] Error reading: " + s);
            }
        }
    }

    private static ICustomTexture[] loadCustomTextures(Properties props, ProgramStage stage)
    {
        String s = "texture." + stage.getName() + ".";
        Set set = props.keySet();
        List<ICustomTexture> list = new ArrayList<>();

        for (String s1 : (Set<String>)(Set<?>)set)
        {
            if (s1.startsWith(s))
            {
                String s2 = StrUtils.removePrefix(s1, s);
                s2 = StrUtils.removeSuffix(s2, new String[] {".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"});
                String s3 = props.getProperty(s1).trim();
                int i = getTextureIndex(stage, s2);

                if (i < 0)
                {
                    SMCLog.warning("Invalid texture name: " + s1);
                }
                else
                {
                    ICustomTexture icustomtexture = loadCustomTexture(i, s3);

                    if (icustomtexture != null)
                    {
                        SMCLog.info("Custom texture: " + s1 + " = " + s3);
                        list.add(icustomtexture);
                    }
                }
            }
        }

        if (list.size() <= 0)
        {
            return null;
        }
        else
        {
            ICustomTexture[] aicustomtexture = list.toArray(new ICustomTexture[list.size()]);
            return aicustomtexture;
        }
    }

    private static ICustomTexture loadCustomTexture(int textureUnit, String path)
    {
        if (path == null)
        {
            return null;
        }
        else
        {
            path = path.trim();

            if (path.indexOf(58) >= 0)
            {
                return loadCustomTextureLocation(textureUnit, path);
            }
            else
            {
                return path.indexOf(32) >= 0 ? loadCustomTextureRaw(textureUnit, path) : loadCustomTextureShaders(textureUnit, path);
            }
        }
    }

    private static ICustomTexture loadCustomTextureLocation(int textureUnit, String path)
    {
        String s = path.trim();
        int i = 0;

        if (s.startsWith("minecraft:textures/"))
        {
            s = StrUtils.addSuffixCheck(s, ".png");

            if (s.endsWith("_n.png"))
            {
                s = StrUtils.replaceSuffix(s, "_n.png", ".png");
                i = 1;
            }
            else if (s.endsWith("_s.png"))
            {
                s = StrUtils.replaceSuffix(s, "_s.png", ".png");
                i = 2;
            }
        }

        if (s.startsWith("minecraft:dynamic/lightmap_"))
        {
            s = s.replace("lightmap", "light_map");
        }

        ResourceLocation resourcelocation = new ResourceLocation(s);
        return new CustomTextureLocation(textureUnit, resourcelocation, i);
    }

    private static void reloadCustomTexturesLocation(ICustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                ICustomTexture icustomtexture = cts[i];

                if (icustomtexture instanceof CustomTextureLocation)
                {
                    CustomTextureLocation customtexturelocation = (CustomTextureLocation)icustomtexture;
                    customtexturelocation.reloadTexture();
                }
            }
        }
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line)
    {
        ConnectedParser connectedparser = new ConnectedParser("Shaders");
        String[] astring = Config.tokenize(line, " ");
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(astring));
        String s = deque.poll();
        TextureType texturetype = (TextureType)connectedparser.parseEnum(deque.poll(), TextureType.values(), "texture type");

        if (texturetype == null)
        {
            SMCLog.warning("Invalid raw texture type: " + line);
            return null;
        }
        else
        {
            InternalFormat internalformat = (InternalFormat)connectedparser.parseEnum(deque.poll(), InternalFormat.values(), "internal format");

            if (internalformat == null)
            {
                SMCLog.warning("Invalid raw texture internal format: " + line);
                return null;
            }
            else
            {
                int i = 0;
                int j = 0;
                int k = 0;

                switch (texturetype)
                {
                    case TEXTURE_1D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_2D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_3D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        k = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_RECTANGLE:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    default:
                        SMCLog.warning("Invalid raw texture type: " + texturetype);
                        return null;
                }

                if (i >= 0 && j >= 0 && k >= 0)
                {
                    PixelFormat pixelformat = (PixelFormat)connectedparser.parseEnum(deque.poll(), PixelFormat.values(), "pixel format");

                    if (pixelformat == null)
                    {
                        SMCLog.warning("Invalid raw texture pixel format: " + line);
                        return null;
                    }
                    else
                    {
                        PixelType pixeltype = (PixelType)connectedparser.parseEnum(deque.poll(), PixelType.values(), "pixel type");

                        if (pixeltype == null)
                        {
                            SMCLog.warning("Invalid raw texture pixel type: " + line);
                            return null;
                        }
                        else if (!deque.isEmpty())
                        {
                            SMCLog.warning("Invalid raw texture, too many parameters: " + line);
                            return null;
                        }
                        else
                        {
                            return loadCustomTextureRaw(textureUnit, line, s, texturetype, internalformat, i, j, k, pixelformat, pixeltype);
                        }
                    }
                }
                else
                {
                    SMCLog.warning("Invalid raw texture size: " + line);
                    return null;
                }
            }
        }
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line, String path, TextureType type, InternalFormat internalFormat, int width, int height, int depth, PixelFormat pixelFormat, PixelType pixelType)
    {
        try
        {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);

            if (inputstream == null)
            {
                SMCLog.warning("Raw texture not found: " + path);
                return null;
            }
            else
            {
                byte[] abyte = Config.readAll(inputstream);
                IOUtils.closeQuietly(inputstream);
                ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(abyte.length);
                bytebuffer.put(abyte);
                ((Buffer)bytebuffer).flip();
                TextureMetadataSection texturemetadatasection = SimpleShaderTexture.loadTextureMetadataSection(s, new TextureMetadataSection(true, true));
                return new CustomTextureRaw(type, internalFormat, width, height, depth, pixelFormat, pixelType, bytebuffer, textureUnit, texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp());
            }
        }
        catch (IOException ioexception)
        {
            SMCLog.warning("Error loading raw texture: " + path);
            SMCLog.warning("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static ICustomTexture loadCustomTextureShaders(int textureUnit, String path)
    {
        path = path.trim();

        if (path.indexOf(46) < 0)
        {
            path = path + ".png";
        }

        try
        {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);

            if (inputstream == null)
            {
                SMCLog.warning("Texture not found: " + path);
                return null;
            }
            else
            {
                IOUtils.closeQuietly(inputstream);
                SimpleShaderTexture simpleshadertexture = new SimpleShaderTexture(s);
                simpleshadertexture.loadTexture(mc.getResourceManager());
                return new CustomTexture(textureUnit, s, simpleshadertexture);
            }
        }
        catch (IOException ioexception)
        {
            SMCLog.warning("Error loading texture: " + path);
            SMCLog.warning("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static int getTextureIndex(ProgramStage stage, String name)
    {
        if (stage == ProgramStage.GBUFFERS)
        {
            int i = ShaderParser.getIndex(name, "colortex", 4, 15);

            if (i >= 0)
            {
                return colorTextureImageUnit[i];
            }

            if (name.equals("texture"))
            {
                return 0;
            }

            if (name.equals("lightmap"))
            {
                return 1;
            }

            if (name.equals("normals"))
            {
                return 2;
            }

            if (name.equals("specular"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0"))
            {
                return 6;
            }

            if (name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        if (stage.isAnyComposite())
        {
            int j = ShaderParser.getIndex(name, "colortex", 0, 15);

            if (j >= 0)
            {
                return colorTextureImageUnit[j];
            }

            if (name.equals("colortex0"))
            {
                return 0;
            }

            if (name.equals("gdepth"))
            {
                return 1;
            }

            if (name.equals("gnormal"))
            {
                return 2;
            }

            if (name.equals("composite"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0") || name.equals("gdepthtex"))
            {
                return 6;
            }

            if (name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 11;
            }

            if (name.equals("depthtex2"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        return -1;
    }

    private static void bindCustomTextures(ICustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                ICustomTexture icustomtexture = cts[i];
                GlStateManager.activeTexture(33984 + icustomtexture.getTextureUnit());
                int j = icustomtexture.getTextureId();
                int k = icustomtexture.getTarget();

                if (k == 3553)
                {
                    GlStateManager.bindTexture(j);
                }
                else
                {
                    GL11.glBindTexture(k, j);
                }
            }

            GlStateManager.activeTexture(33984);
        }
    }

    private static void resetCustomTextures()
    {
        deleteCustomTextures(customTexturesGbuffers);
        deleteCustomTextures(customTexturesComposite);
        deleteCustomTextures(customTexturesDeferred);
        deleteCustomTextures(customTexturesShadowcomp);
        deleteCustomTextures(customTexturesPrepare);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
        customTexturesShadowcomp = null;
        customTexturesPrepare = null;
    }

    private static void deleteCustomTextures(ICustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                ICustomTexture icustomtexture = cts[i];
                icustomtexture.deleteTexture();
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName)
    {
        ShaderOption[] ashaderoption = (ShaderOption[])shaderPackOptions.clone();

        if (shaderPackGuiScreens == null)
        {
            if (shaderPackProfiles != null)
            {
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderPackProfiles, ashaderoption);
                ashaderoption = (ShaderOption[])Config.addObjectToArray(ashaderoption, shaderoptionprofile, 0);
            }

            return getVisibleOptions(ashaderoption);
        }
        else
        {
            String s = screenName != null ? "screen." + screenName : "screen";
            ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);

            if (screenshaderoptions == null)
            {
                return new ShaderOption[0];
            }
            else
            {
                ShaderOption[] ashaderoption1 = screenshaderoptions.getShaderOptions();
                List<ShaderOption> list = new ArrayList<>();

                for (int i = 0; i < ashaderoption1.length; ++i)
                {
                    ShaderOption shaderoption = ashaderoption1[i];

                    if (shaderoption == null)
                    {
                        list.add((ShaderOption)null);
                    }
                    else if (shaderoption instanceof ShaderOptionRest)
                    {
                        ShaderOption[] ashaderoption2 = getShaderOptionsRest(shaderPackGuiScreens, ashaderoption);
                        list.addAll(Arrays.asList(ashaderoption2));
                    }
                    else
                    {
                        list.add(shaderoption);
                    }
                }

                return list.toArray(new ShaderOption[list.size()]);
            }
        }
    }

    public static int getShaderPackColumns(String screenName, int def)
    {
        String s = screenName != null ? "screen." + screenName : "screen";

        if (shaderPackGuiScreens == null)
        {
            return def;
        }
        else
        {
            ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);
            return screenshaderoptions == null ? def : screenshaderoptions.getColumns();
        }
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> mapScreens, ShaderOption[] ops)
    {
        Set<String> set = new HashSet<>();

        for (String s : mapScreens.keySet())
        {
            ScreenShaderOptions screenshaderoptions = mapScreens.get(s);
            ShaderOption[] ashaderoption = screenshaderoptions.getShaderOptions();

            for (int i = 0; i < ashaderoption.length; ++i)
            {
                ShaderOption shaderoption = ashaderoption[i];

                if (shaderoption != null)
                {
                    set.add(shaderoption.getName());
                }
            }
        }

        List<ShaderOption> list = new ArrayList<>();

        for (int j = 0; j < ops.length; ++j)
        {
            ShaderOption shaderoption1 = ops[j];

            if (shaderoption1.isVisible())
            {
                String s1 = shaderoption1.getName();

                if (!set.contains(s1))
                {
                    list.add(shaderoption1);
                }
            }
        }

        return list.toArray(new ShaderOption[list.size()]);
    }

    public static ShaderOption getShaderOption(String name)
    {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions()
    {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String name)
    {
        return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(name);
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops)
    {
        List<ShaderOption> list = new ArrayList<>();

        for (int i = 0; i < ops.length; ++i)
        {
            ShaderOption shaderoption = ops[i];

            if (shaderoption.isVisible())
            {
                list.add(shaderoption);
            }
        }

        return list.toArray(new ShaderOption[list.size()]);
    }

    public static void saveShaderPackOptions()
    {
        saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp)
    {
        Properties properties = new PropertiesOrdered();

        if (shaderPackOptions != null)
        {
            for (int i = 0; i < sos.length; ++i)
            {
                ShaderOption shaderoption = sos[i];

                if (shaderoption.isChanged() && shaderoption.isEnabled())
                {
                    properties.setProperty(shaderoption.getName(), shaderoption.getValue());
                }
            }
        }

        try
        {
            saveOptionProperties(sp, properties);
        }
        catch (IOException ioexception)
        {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException
    {
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getInstance().gameDir, s);

        if (props.isEmpty())
        {
            file1.delete();
        }
        else
        {
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            props.store(fileoutputstream, (String)null);
            fileoutputstream.flush();
            fileoutputstream.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions()
    {
        try
        {
            String[] astring = programs.getProgramNames();
            Properties properties = loadOptionProperties(shaderPack);
            ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, astring, shaderPackDimensions);

            for (int i = 0; i < ashaderoption.length; ++i)
            {
                ShaderOption shaderoption = ashaderoption[i];
                String s = properties.getProperty(shaderoption.getName());

                if (s != null)
                {
                    shaderoption.resetValue();

                    if (!shaderoption.setValue(s))
                    {
                        Config.warn("[Shaders] Invalid value, option: " + shaderoption.getName() + ", value: " + s);
                    }
                }
            }

            return ashaderoption;
        }
        catch (IOException ioexception)
        {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp) throws IOException
    {
        Properties properties = new PropertiesOrdered();
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getInstance().gameDir, s);

        if (file1.exists() && file1.isFile() && file1.canRead())
        {
            FileInputStream fileinputstream = new FileInputStream(file1);
            properties.load(fileinputstream);
            fileinputstream.close();
            return properties;
        }
        else
        {
            return properties;
        }
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops)
    {
        List<ShaderOption> list = new ArrayList<>();

        for (int i = 0; i < ops.length; ++i)
        {
            ShaderOption shaderoption = ops[i];

            if (shaderoption.isEnabled() && shaderoption.isChanged())
            {
                list.add(shaderoption);
            }
        }

        return list.toArray(new ShaderOption[list.size()]);
    }

    private static String applyOptions(String line, ShaderOption[] ops)
    {
        if (ops != null && ops.length > 0)
        {
            for (int i = 0; i < ops.length; ++i)
            {
                ShaderOption shaderoption = ops[i];

                if (shaderoption.matchesLine(line))
                {
                    line = shaderoption.getSourceLine();
                    break;
                }
            }

            return line;
        }
        else
        {
            return line;
        }
    }

    public static ArrayList listOfShaders()
    {
        ArrayList<String> arraylist = new ArrayList<>();
        ArrayList<String> arraylist1 = new ArrayList<>();

        try
        {
            if (!shaderPacksDir.exists())
            {
                shaderPacksDir.mkdir();
            }

            File[] afile = shaderPacksDir.listFiles();

            for (int i = 0; i < afile.length; ++i)
            {
                File file1 = afile[i];
                String s = file1.getName();

                if (file1.isDirectory())
                {
                    if (!s.equals("debug"))
                    {
                        File file2 = new File(file1, "shaders");

                        if (file2.exists() && file2.isDirectory())
                        {
                            arraylist.add(s);
                        }
                    }
                }
                else if (file1.isFile() && s.toLowerCase().endsWith(".zip"))
                {
                    arraylist1.add(s);
                }
            }
        }
        catch (Exception exception)
        {
        }

        Collections.sort(arraylist, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(arraylist1, String.CASE_INSENSITIVE_ORDER);
        ArrayList<String> arraylist2 = new ArrayList<>();
        arraylist2.add("OFF");
        arraylist2.add("(internal)");
        arraylist2.addAll(arraylist);
        arraylist2.addAll(arraylist1);
        return arraylist2;
    }

    public static int checkFramebufferStatus(String location)
    {
        int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (i != 36053)
        {
            SMCLog.severe("FramebufferStatus 0x%04X at '%s'", i, location);
        }

        return i;
    }

    public static int checkGLError(String location)
    {
        int i = GlStateManager.getError();

        if (i != 0 && GlErrors.isEnabled(i))
        {
            String s = Config.getGlErrorString(i);
            String s1 = getErrorInfo(i, location);
            String s2 = String.format("OpenGL error: %s (%s)%s, at: %s", i, s, s1, location);
            SMCLog.severe(s2);

            if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorShaders", 10000L))
            {
                String s3 = I18n.format("of.message.openglError", i, s);
                printChat(s3);
            }
        }

        return i;
    }

    private static String getErrorInfo(int errorCode, String location)
    {
        StringBuilder stringbuilder = new StringBuilder();

        if (errorCode == 1286)
        {
            int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            String s = getFramebufferStatusText(i);
            String s1 = ", fbStatus: " + i + " (" + s + ")";
            stringbuilder.append(s1);
        }

        String s2 = activeProgram.getName();

        if (s2.isEmpty())
        {
            s2 = "none";
        }

        stringbuilder.append(", program: " + s2);
        Program program = getProgramById(activeProgramID);

        if (program != activeProgram)
        {
            String s3 = program.getName();

            if (s3.isEmpty())
            {
                s3 = "none";
            }

            stringbuilder.append(" (" + s3 + ")");
        }

        if (location.equals("setDrawBuffers"))
        {
            stringbuilder.append(", drawBuffers: " + ArrayUtils.arrayToString((Object[])activeProgram.getDrawBufSettings()));
        }

        return stringbuilder.toString();
    }

    private static Program getProgramById(int programID)
    {
        for (int i = 0; i < ProgramsAll.length; ++i)
        {
            Program program = ProgramsAll[i];

            if (program.getId() == programID)
            {
                return program;
            }
        }

        return ProgramNone;
    }

    private static String getFramebufferStatusText(int fbStatusCode)
    {
        switch (fbStatusCode)
        {
            case 33305:
                return "Undefined";

            case 36053:
                return "Complete";

            case 36054:
                return "Incomplete attachment";

            case 36055:
                return "Incomplete missing attachment";

            case 36059:
                return "Incomplete draw buffer";

            case 36060:
                return "Incomplete read buffer";

            case 36061:
                return "Unsupported";

            case 36182:
                return "Incomplete multisample";

            case 36264:
                return "Incomplete layer targets";

            default:
                return "Unknown";
        }
    }

    private static void printChat(String str)
    {
        mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(str));
    }

    public static void printChatAndLogError(String str)
    {
        SMCLog.severe(str);
        mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf)
    {
        StringBuilder stringbuilder = new StringBuilder(128);
        stringbuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int i = buf.limit();

        for (int j = 0; j < i; ++j)
        {
            stringbuilder.append(" ").append(buf.get(j));
        }

        stringbuilder.append("]");
        SMCLog.info(stringbuilder.toString());
    }

    public static void startup(Minecraft mc)
    {
        checkShadersModInstalled();
        Shaders.mc = mc;
        mc = Minecraft.getInstance();
        capabilities = GL.getCapabilities();
        glVersionString = GL11.glGetString(GL11.GL_VERSION);
        glVendorString = GL11.glGetString(GL11.GL_VENDOR);
        glRendererString = GL11.glGetString(GL11.GL_RENDERER);
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (capabilities.OpenGL20 ? " 2.0 " : " - ") + (capabilities.OpenGL21 ? " 2.1 " : " - ") + (capabilities.OpenGL30 ? " 3.0 " : " - ") + (capabilities.OpenGL32 ? " 3.2 " : " - ") + (capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL43.glGetInteger(34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL43.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL43.glGetInteger(34930));
        hasGlGenMipmap = capabilities.OpenGL30;
        glDebugGroups = Boolean.getBoolean("gl.debug.groups") && capabilities.GL_KHR_debug;

        if (glDebugGroups)
        {
            SMCLog.info("glDebugGroups: true");
        }

        loadConfig();
    }

    public static void updateBlockLightLevel()
    {
        if (isOldLighting())
        {
            blockLightLevel05 = 0.5F;
            blockLightLevel06 = 0.6F;
            blockLightLevel08 = 0.8F;
        }
        else
        {
            blockLightLevel05 = 1.0F;
            blockLightLevel06 = 1.0F;
            blockLightLevel08 = 1.0F;
        }
    }

    public static boolean isOldHandLight()
    {
        if (!configOldHandLight.isDefault())
        {
            return configOldHandLight.isTrue();
        }
        else
        {
            return !shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true;
        }
    }

    public static boolean isDynamicHandLight()
    {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting()
    {
        if (!configOldLighting.isDefault())
        {
            return configOldLighting.isTrue();
        }
        else
        {
            return !shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true;
        }
    }

    public static boolean isRenderShadowTerrain()
    {
        return !shaderPackShadowTerrain.isFalse();
    }

    public static boolean isRenderShadowTranslucent()
    {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isRenderShadowEntities()
    {
        return !shaderPackShadowEntities.isFalse();
    }

    public static boolean isRenderShadowBlockEntities()
    {
        return !shaderPackShadowBlockEntities.isFalse();
    }

    public static boolean isUnderwaterOverlay()
    {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun()
    {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon()
    {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette()
    {
        return !shaderPackVignette.isFalse();
    }

    public static boolean isRenderBackFace(RenderType blockLayerIn)
    {
        if (blockLayerIn == RenderTypes.SOLID)
        {
            return shaderPackBackFaceSolid.isTrue();
        }
        else if (blockLayerIn == RenderTypes.CUTOUT)
        {
            return shaderPackBackFaceCutout.isTrue();
        }
        else if (blockLayerIn == RenderTypes.CUTOUT_MIPPED)
        {
            return shaderPackBackFaceCutoutMipped.isTrue();
        }
        else
        {
            return blockLayerIn == RenderTypes.TRANSLUCENT ? shaderPackBackFaceTranslucent.isTrue() : false;
        }
    }

    public static boolean isRainDepth()
    {
        return shaderPackRainDepth.isTrue();
    }

    public static boolean isBeaconBeamDepth()
    {
        return shaderPackBeaconBeamDepth.isTrue();
    }

    public static boolean isSeparateAo()
    {
        return shaderPackSeparateAo.isTrue();
    }

    public static boolean isFrustumCulling()
    {
        return !shaderPackFrustumCulling.isFalse();
    }

    public static void init()
    {
        boolean flag;

        if (!isInitializedOnce)
        {
            isInitializedOnce = true;
            flag = true;
        }
        else
        {
            flag = false;
        }

        if (!isShaderPackInitialized)
        {
            checkGLError("Shaders.init pre");

            if (getShaderPackName() != null)
            {
            }

            if (!capabilities.OpenGL20)
            {
                printChatAndLogError("No OpenGL 2.0");
            }

            if (!capabilities.GL_EXT_framebuffer_object)
            {
                printChatAndLogError("No EXT_framebuffer_object");
            }

            dfbDrawBuffers.position(0).limit(8);
            sfbDrawBuffers.position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            bindImageTextures = false;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(gbuffersClearColor, (Object)null);
            Arrays.fill(shadowBuffersFormat, 6408);
            Arrays.fill(shadowBuffersClear, true);
            Arrays.fill(shadowBuffersClearColor, (Object)null);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0F;
            shadowIntervalSize = 2.0F;
            shadowMapWidth = 1024;
            shadowMapHeight = 1024;
            spShadowMapWidth = 1024;
            spShadowMapHeight = 1024;
            shadowMapFOV = 90.0F;
            shadowMapHalfPlane = 160.0F;
            shadowMapIsOrtho = true;
            shadowDistanceRenderMul = -1.0F;
            aoLevel = -1.0F;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useTangentAttrib = false;
            useVelocityAttrib = false;
            waterShadowEnabled = false;
            hasGeometryShaders = false;
            updateBlockLightLevel();
            Smoother.resetValues();
            shaderUniforms.reset();

            if (customUniforms != null)
            {
                customUniforms.reset();
            }

            ShaderProfile shaderprofile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String s = "";

            if (currentWorld != null)
            {
                int i = WorldUtils.getDimensionId(currentWorld.getDimensionKey());

                if (shaderPackDimensions.contains(i))
                {
                    s = "world" + i + "/";
                }
            }

            loadShaderPackDynamicProperties();

            for (int k = 0; k < ProgramsAll.length; ++k)
            {
                Program program = ProgramsAll[k];
                program.resetId();
                program.resetConfiguration();

                if (program.getProgramStage() != ProgramStage.NONE)
                {
                    String s1 = program.getName();
                    String s2 = s + s1;
                    boolean flag1 = true;

                    if (shaderPackProgramConditions.containsKey(s2))
                    {
                        flag1 = flag1 && shaderPackProgramConditions.get(s2).eval();
                    }

                    if (shaderprofile != null)
                    {
                        flag1 = flag1 && !shaderprofile.isProgramDisabled(s2);
                    }

                    if (!flag1)
                    {
                        SMCLog.info("Program disabled: " + s2);
                        s1 = "<disabled>";
                        s2 = s + s1;
                    }

                    String s3 = "/shaders/" + s2;
                    String s4 = s3 + ".vsh";
                    String s5 = s3 + ".gsh";
                    String s6 = s3 + ".fsh";
                    ComputeProgram[] acomputeprogram = setupComputePrograms(program, "/shaders/", s2, ".csh");
                    program.setComputePrograms(acomputeprogram);
                    Config.sleep(10L);
                    setupProgram(program, s4, s5, s6);
                    int j = program.getId();

                    if (j > 0)
                    {
                        SMCLog.info("Program loaded: " + s2);
                    }

                    initDrawBuffers(program);
                    initBlendStatesIndexed(program);
                    updateToggleBuffers(program);
                    updateProgramSize(program);
                }
            }

            hasDeferredPrograms = ProgramUtils.hasActive(ProgramsDeferred);
            hasShadowcompPrograms = ProgramUtils.hasActive(ProgramsShadowcomp);
            hasPreparePrograms = ProgramUtils.hasActive(ProgramsPrepare);
            usedColorAttachs = usedColorBuffers;

            if (usedShadowDepthBuffers > 0 || usedShadowColorBuffers > 0)
            {
                hasShadowMap = true;
                usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, 1);
            }

            shouldSkipDefaultShadow = hasShadowMap;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            SMCLog.info("bindImageTextures: " + bindImageTextures);
            int l = GL43.glGetInteger(34852);

            if (usedDrawBuffers > l)
            {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + l);
                usedDrawBuffers = l;
            }

            dfbDrawBuffers.position(0).limit(usedDrawBuffers);

            for (int i1 = 0; i1 < usedDrawBuffers; ++i1)
            {
                dfbDrawBuffers.put(i1, 36064 + i1);
            }

            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);

            for (int j1 = 0; j1 < usedShadowColorBuffers; ++j1)
            {
                sfbDrawBuffers.put(j1, 36064 + j1);
            }

            for (int k1 = 0; k1 < ProgramsAll.length; ++k1)
            {
                Program program1 = ProgramsAll[k1];
                Program program2;

                for (program2 = program1; program2.getId() == 0 && program2.getProgramBackup() != program2; program2 = program2.getProgramBackup())
                {
                }

                if (program2 != program1 && program1 != ProgramShadow)
                {
                    program1.copyFrom(program2);
                }
            }

            resize();
            resizeShadow();

            if (noiseTextureEnabled)
            {
                setupNoiseTexture();
            }

            if (defaultTexture == null)
            {
                defaultTexture = ShadersTex.createDefaultTexture();
            }

            MatrixStack matrixstack = new MatrixStack();
            matrixstack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
            preCelestialRotate(matrixstack);
            postCelestialRotate(matrixstack);
            isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayLists();

            if (!flag)
            {
            }

            checkGLError("Shaders.init");
        }
    }

    private static void initDrawBuffers(Program p)
    {
        int i = GL43.glGetInteger(34852);
        Arrays.fill(p.getToggleColorTextures(), false);

        if (p == ProgramFinal)
        {
            p.setDrawBuffers((DrawBuffers)null);
        }
        else if (p.getId() == 0)
        {
            if (p == ProgramShadow)
            {
                p.setDrawBuffers(drawBuffersNone);
            }
            else
            {
                p.setDrawBuffers(drawBuffersColorAtt[0]);
            }
        }
        else
        {
            String[] astring = p.getDrawBufSettings();

            if (astring == null)
            {
                if (p != ProgramShadow && p != ProgramShadowSolid && p != ProgramShadowCutout)
                {
                    p.setDrawBuffers(dfbDrawBuffers);
                    usedDrawBuffers = Math.min(usedColorBuffers, i);
                    Arrays.fill(p.getToggleColorTextures(), 0, usedColorBuffers, true);
                }
                else
                {
                    p.setDrawBuffers(sfbDrawBuffers);
                }
            }
            else
            {
                DrawBuffers drawbuffers = p.getDrawBuffersCustom();
                int j = astring.length;
                usedDrawBuffers = Math.max(usedDrawBuffers, j);
                j = Math.min(j, i);
                p.setDrawBuffers(drawbuffers);
                drawbuffers.limit(j);

                for (int k = 0; k < j; ++k)
                {
                    int l = getDrawBuffer(p, astring[k]);
                    drawbuffers.put(k, l);
                }

                String s = drawbuffers.getInfo(false);
                String s1 = drawbuffers.getInfo(true);

                if (!Config.equals(s, s1))
                {
                    SMCLog.info("Draw buffers: " + s + " -> " + s1);
                }
            }
        }
    }

    private static void initBlendStatesIndexed(Program p)
    {
        GlBlendState[] aglblendstate = p.getBlendStatesColorIndexed();

        if (aglblendstate != null)
        {
            for (int i = 0; i < aglblendstate.length; ++i)
            {
                GlBlendState glblendstate = aglblendstate[i];

                if (glblendstate != null)
                {
                    String s = Integer.toHexString(i).toUpperCase();
                    int j = 36064 + i;
                    int k = p.getDrawBuffers().indexOf(j);

                    if (k < 0)
                    {
                        SMCLog.warning("Blend buffer not used in draw buffers: " + s);
                    }
                    else
                    {
                        p.setBlendStateIndexed(k, glblendstate);
                        SMCLog.info("Blend buffer: " + s);
                    }
                }
            }
        }
    }

    private static int getDrawBuffer(Program p, String str)
    {
        int i = 0;
        int j = Config.parseInt(str, -1);

        if (p == ProgramShadow)
        {
            if (j >= 0 && j < 2)
            {
                i = 36064 + j;
                usedShadowColorBuffers = Math.max(usedShadowColorBuffers, j + 1);
            }

            return i;
        }
        else
        {
            if (j >= 0 && j < 16)
            {
                p.getToggleColorTextures()[j] = true;
                i = 36064 + j;
                usedColorAttachs = Math.max(usedColorAttachs, j + 1);
                usedColorBuffers = Math.max(usedColorBuffers, j + 1);
            }

            return i;
        }
    }

    private static void updateToggleBuffers(Program p)
    {
        boolean[] aboolean = p.getToggleColorTextures();
        Boolean[] aboolean1 = p.getBuffersFlip();

        for (int i = 0; i < aboolean1.length; ++i)
        {
            Boolean obool = aboolean1[i];

            if (obool != null)
            {
                aboolean[i] = obool;
            }
        }
    }

    private static void updateProgramSize(Program p)
    {
        if (p.getProgramStage().isMainComposite())
        {
            DynamicDimension dynamicdimension = null;
            int i = 0;
            int j = 0;
            DrawBuffers drawbuffers = p.getDrawBuffers();

            if (drawbuffers != null)
            {
                for (int k = 0; k < drawbuffers.limit(); ++k)
                {
                    int l = drawbuffers.get(k);
                    int i1 = l - 36064;

                    if (i1 >= 0 && i1 < colorBufferSizes.length)
                    {
                        DynamicDimension dynamicdimension1 = colorBufferSizes[i1];

                        if (dynamicdimension1 != null)
                        {
                            ++i;

                            if (dynamicdimension == null)
                            {
                                dynamicdimension = dynamicdimension1;
                            }

                            if (dynamicdimension1.equals(dynamicdimension))
                            {
                                ++j;
                            }
                        }
                    }
                }

                if (i != 0)
                {
                    if (j != drawbuffers.limit())
                    {
                        SMCLog.severe("Program " + p.getName() + " draws to buffers with different sizes");
                    }
                    else
                    {
                        p.setDrawSize(dynamicdimension);
                    }
                }
            }
        }
    }

    public static void resetDisplayLists()
    {
        SMCLog.info("Reset model renderers");
        ++countResetDisplayLists;
        SMCLog.info("Reset world renderers");
        mc.worldRenderer.loadRenderers();
    }

    private static void setupProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath)
    {
        checkGLError("pre setupProgram");
        progUseEntityAttrib = false;
        progUseMidTexCoordAttrib = false;
        progUseTangentAttrib = false;
        progUseVelocityAttrib = false;
        progUseMidBlockAttrib = false;
        int i = createVertShader(program, vShaderPath);
        int j = createGeomShader(program, gShaderPath);
        int k = createFragShader(program, fShaderPath);
        checkGLError("create");

        if (i != 0 || j != 0 || k != 0)
        {
            int l = ARBShaderObjects.glCreateProgramObjectARB();
            checkGLError("create");

            if (i != 0)
            {
                ARBShaderObjects.glAttachObjectARB(l, i);
                checkGLError("attach");
            }

            if (j != 0)
            {
                ARBShaderObjects.glAttachObjectARB(l, j);
                checkGLError("attach");

                if (progArbGeometryShader4)
                {
                    ARBGeometryShader4.glProgramParameteriARB(l, 36315, 4);
                    ARBGeometryShader4.glProgramParameteriARB(l, 36316, 5);
                    ARBGeometryShader4.glProgramParameteriARB(l, 36314, progMaxVerticesOut);
                    checkGLError("arbGeometryShader4");
                }

                if (progExtGeometryShader4)
                {
                    EXTGeometryShader4.glProgramParameteriEXT(l, 36315, 4);
                    EXTGeometryShader4.glProgramParameteriEXT(l, 36316, 5);
                    EXTGeometryShader4.glProgramParameteriEXT(l, 36314, progMaxVerticesOut);
                    checkGLError("extGeometryShader4");
                }

                hasGeometryShaders = true;
            }

            if (k != 0)
            {
                ARBShaderObjects.glAttachObjectARB(l, k);
                checkGLError("attach");
            }

            if (progUseEntityAttrib)
            {
                ARBVertexShader.glBindAttribLocationARB(l, entityAttrib, "mc_Entity");
                checkGLError("mc_Entity");
            }

            if (progUseMidTexCoordAttrib)
            {
                ARBVertexShader.glBindAttribLocationARB(l, midTexCoordAttrib, "mc_midTexCoord");
                checkGLError("mc_midTexCoord");
            }

            if (progUseTangentAttrib)
            {
                ARBVertexShader.glBindAttribLocationARB(l, tangentAttrib, "at_tangent");
                checkGLError("at_tangent");
            }

            if (progUseVelocityAttrib)
            {
                ARBVertexShader.glBindAttribLocationARB(l, velocityAttrib, "at_velocity");
                checkGLError("at_velocity");
            }

            if (progUseMidBlockAttrib)
            {
                ARBVertexShader.glBindAttribLocationARB(l, midBlockAttrib, "at_midBlock");
                checkGLError("at_midBlock");
            }

            ARBShaderObjects.glLinkProgramARB(l);

            if (GL43.glGetProgrami(l, 35714) != 1)
            {
                SMCLog.severe("Error linking program: " + l + " (" + program.getName() + ")");
            }

            printLogInfo(l, program.getName());

            if (i != 0)
            {
                ARBShaderObjects.glDetachObjectARB(l, i);
                ARBShaderObjects.glDeleteObjectARB(i);
            }

            if (j != 0)
            {
                ARBShaderObjects.glDetachObjectARB(l, j);
                ARBShaderObjects.glDeleteObjectARB(j);
            }

            if (k != 0)
            {
                ARBShaderObjects.glDetachObjectARB(l, k);
                ARBShaderObjects.glDeleteObjectARB(k);
            }

            program.setId(l);
            program.setRef(l);
            useProgram(program);
            ARBShaderObjects.glValidateProgramARB(l);
            useProgram(ProgramNone);
            printLogInfo(l, program.getName());
            int i1 = GL43.glGetProgrami(l, 35715);

            if (i1 != 1)
            {
                String s = "\"";
                printChatAndLogError("[Shaders] Error: Invalid program " + s + program.getName() + s);
                ARBShaderObjects.glDeleteObjectARB(l);
                l = 0;
                program.resetId();
            }
        }
    }

    private static ComputeProgram[] setupComputePrograms(Program program, String prefixShaders, String programPath, String shaderExt)
    {
        if (program.getProgramStage() == ProgramStage.GBUFFERS)
        {
            return new ComputeProgram[0];
        }
        else
        {
            List<ComputeProgram> list = new ArrayList<>();
            int i = 27;

            for (int j = 0; j < i; ++j)
            {
                String s = j > 0 ? "_" + (char)(97 + j - 1) : "";
                String s1 = programPath + s;
                String s2 = prefixShaders + s1 + shaderExt;
                ComputeProgram computeprogram = new ComputeProgram(program.getName(), program.getProgramStage());
                setupComputeProgram(computeprogram, s2);

                if (computeprogram.getId() > 0)
                {
                    list.add(computeprogram);
                    SMCLog.info("Compute program loaded: " + s1);
                }
            }

            return list.toArray(new ComputeProgram[list.size()]);
        }
    }

    private static void setupComputeProgram(ComputeProgram program, String cShaderPath)
    {
        checkGLError("pre setupProgram");
        int i = createCompShader(program, cShaderPath);
        checkGLError("create");

        if (i != 0)
        {
            int j = ARBShaderObjects.glCreateProgramObjectARB();
            checkGLError("create");

            if (i != 0)
            {
                ARBShaderObjects.glAttachObjectARB(j, i);
                checkGLError("attach");
            }

            ARBShaderObjects.glLinkProgramARB(j);

            if (GL43.glGetProgrami(j, 35714) != 1)
            {
                SMCLog.severe("Error linking program: " + j + " (" + program.getName() + ")");
            }

            printLogInfo(j, program.getName());

            if (i != 0)
            {
                ARBShaderObjects.glDetachObjectARB(j, i);
                ARBShaderObjects.glDeleteObjectARB(i);
            }

            program.setId(j);
            program.setRef(j);
            ARBShaderObjects.glUseProgramObjectARB(j);
            ARBShaderObjects.glValidateProgramARB(j);
            ARBShaderObjects.glUseProgramObjectARB(0);
            printLogInfo(j, program.getName());
            int k = GL43.glGetProgrami(j, 35715);

            if (k != 1)
            {
                String s = "\"";
                printChatAndLogError("[Shaders] Error: Invalid program " + s + program.getName() + s);
                ARBShaderObjects.glDeleteObjectARB(j);
                j = 0;
                program.resetId();
            }
        }
    }

    private static int createCompShader(ComputeProgram program, String filename)
    {
        InputStream inputstream = shaderPack.getResourceAsStream(filename);

        if (inputstream == null)
        {
            return 0;
        }
        else
        {
            int i = ARBShaderObjects.glCreateShaderObjectARB(37305);

            if (i == 0)
            {
                return 0;
            }
            else
            {
                ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
                List<String> list = new ArrayList<>();
                LineBuffer linebuffer = new LineBuffer();

                if (linebuffer != null)
                {
                    try
                    {
                        LineBuffer linebuffer1 = LineBuffer.readAll(new InputStreamReader(inputstream));
                        linebuffer1 = ShaderPackParser.resolveIncludes(linebuffer1, filename, shaderPack, 0, list, 0);
                        MacroState macrostate = new MacroState();

                        for (String s : linebuffer1)
                        {
                            s = applyOptions(s, ashaderoption);
                            linebuffer.add(s);

                            if (macrostate.processLine(s))
                            {
                                ShaderLine shaderline = ShaderParser.parseLine(s);

                                if (shaderline != null)
                                {
                                    if (shaderline.isUniform())
                                    {
                                        String s1 = shaderline.getName();
                                        int j;

                                        if ((j = ShaderParser.getShadowDepthIndex(s1)) >= 0)
                                        {
                                            usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, j + 1);
                                        }
                                        else if ((j = ShaderParser.getShadowColorIndex(s1)) >= 0)
                                        {
                                            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, j + 1);
                                        }
                                        else if ((j = ShaderParser.getShadowColorImageIndex(s1)) >= 0)
                                        {
                                            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, j + 1);
                                            bindImageTextures = true;
                                        }
                                        else if ((j = ShaderParser.getDepthIndex(s1)) >= 0)
                                        {
                                            usedDepthBuffers = Math.max(usedDepthBuffers, j + 1);
                                        }
                                        else if ((j = ShaderParser.getColorIndex(s1)) >= 0)
                                        {
                                            usedColorBuffers = Math.max(usedColorBuffers, j + 1);
                                        }
                                        else if ((j = ShaderParser.getColorImageIndex(s1)) >= 0)
                                        {
                                            usedColorBuffers = Math.max(usedColorBuffers, j + 1);
                                            bindImageTextures = true;
                                        }
                                    }
                                    else if (shaderline.isLayout("in"))
                                    {
                                        Vector3i vector3i = ShaderParser.parseLocalSize(shaderline.getValue());

                                        if (vector3i != null)
                                        {
                                            program.setLocalSize(vector3i);
                                        }
                                        else
                                        {
                                            SMCLog.severe("Invalid local size: " + s);
                                        }
                                    }
                                    else if (shaderline.isConstIVec3("workGroups"))
                                    {
                                        Vector3i vector3i1 = shaderline.getValueIVec3();

                                        if (vector3i1 != null)
                                        {
                                            program.setWorkGroups(vector3i1);
                                        }
                                        else
                                        {
                                            SMCLog.severe("Invalid workGroups: " + s);
                                        }
                                    }
                                    else if (shaderline.isConstVec2("workGroupsRender"))
                                    {
                                        Vector2f vector2f = shaderline.getValueVec2();

                                        if (vector2f != null)
                                        {
                                            program.setWorkGroupsRender(vector2f);
                                        }
                                        else
                                        {
                                            SMCLog.severe("Invalid workGroupsRender: " + s);
                                        }
                                    }
                                    else if (shaderline.isConstBoolSuffix("MipmapEnabled", true))
                                    {
                                        String s3 = StrUtils.removeSuffix(shaderline.getName(), "MipmapEnabled");
                                        int l = getBufferIndex(s3);

                                        if (l >= 0)
                                        {
                                            int k = program.getCompositeMipmapSetting();
                                            k = k | 1 << l;
                                            program.setCompositeMipmapSetting(k);
                                            SMCLog.info("%s mipmap enabled", s3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception exception)
                    {
                        SMCLog.severe("Couldn't read " + filename + "!");
                        exception.printStackTrace();
                        ARBShaderObjects.glDeleteObjectARB(i);
                        return 0;
                    }
                }

                String s2 = linebuffer.toString();

                if (saveFinalShaders)
                {
                    saveShader(filename, s2);
                }

                if (program.getLocalSize() == null)
                {
                    SMCLog.severe("Missing local size: " + filename);
                    GL43.glDeleteShader(i);
                    return 0;
                }
                else
                {
                    ARBShaderObjects.glShaderSourceARB(i, (CharSequence)s2);
                    ARBShaderObjects.glCompileShaderARB(i);

                    if (GL43.glGetShaderi(i, 35713) != 1)
                    {
                        SMCLog.severe("Error compiling compute shader: " + filename);
                    }

                    printShaderLogInfo(i, filename, list);
                    return i;
                }
            }
        }
    }

    private static int createVertShader(Program program, String filename)
    {
        InputStream inputstream = shaderPack.getResourceAsStream(filename);

        if (inputstream == null)
        {
            return 0;
        }
        else
        {
            int i = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);

            if (i == 0)
            {
                return 0;
            }
            else
            {
                ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
                List<String> list = new ArrayList<>();
                LineBuffer linebuffer = new LineBuffer();

                if (linebuffer != null)
                {
                    try
                    {
                        LineBuffer linebuffer1 = LineBuffer.readAll(new InputStreamReader(inputstream));
                        linebuffer1 = ShaderPackParser.resolveIncludes(linebuffer1, filename, shaderPack, 0, list, 0);
                        linebuffer1 = ShaderPackParser.remapTextureUnits(linebuffer1);
                        MacroState macrostate = new MacroState();

                        for (String s : linebuffer1)
                        {
                            s = applyOptions(s, ashaderoption);
                            linebuffer.add(s);

                            if (macrostate.processLine(s))
                            {
                                ShaderLine shaderline = ShaderParser.parseLine(s);

                                if (shaderline != null)
                                {
                                    if (shaderline.isAttribute("mc_Entity"))
                                    {
                                        useEntityAttrib = true;
                                        progUseEntityAttrib = true;
                                    }
                                    else if (shaderline.isAttribute("mc_midTexCoord"))
                                    {
                                        useMidTexCoordAttrib = true;
                                        progUseMidTexCoordAttrib = true;
                                    }
                                    else if (shaderline.isAttribute("at_tangent"))
                                    {
                                        useTangentAttrib = true;
                                        progUseTangentAttrib = true;
                                    }
                                    else if (shaderline.isAttribute("at_velocity"))
                                    {
                                        useVelocityAttrib = true;
                                        progUseVelocityAttrib = true;
                                    }
                                    else if (shaderline.isAttribute("at_midBlock"))
                                    {
                                        useMidBlockAttrib = true;
                                        progUseMidBlockAttrib = true;
                                    }

                                    if (shaderline.isConstInt("countInstances"))
                                    {
                                        program.setCountInstances(shaderline.getValueInt());
                                        SMCLog.info("countInstances: " + program.getCountInstances());
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception exception)
                    {
                        SMCLog.severe("Couldn't read " + filename + "!");
                        exception.printStackTrace();
                        ARBShaderObjects.glDeleteObjectARB(i);
                        return 0;
                    }
                }

                String s1 = linebuffer.toString();

                if (saveFinalShaders)
                {
                    saveShader(filename, s1);
                }

                ARBShaderObjects.glShaderSourceARB(i, (CharSequence)s1);
                ARBShaderObjects.glCompileShaderARB(i);

                if (GL43.glGetShaderi(i, 35713) != 1)
                {
                    SMCLog.severe("Error compiling vertex shader: " + filename);
                }

                printShaderLogInfo(i, filename, list);
                return i;
            }
        }
    }

    private static int createGeomShader(Program program, String filename)
    {
        InputStream inputstream = shaderPack.getResourceAsStream(filename);

        if (inputstream == null)
        {
            return 0;
        }
        else
        {
            int i = ARBShaderObjects.glCreateShaderObjectARB(36313);

            if (i == 0)
            {
                return 0;
            }
            else
            {
                ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
                List<String> list = new ArrayList<>();
                progArbGeometryShader4 = false;
                progExtGeometryShader4 = false;
                progMaxVerticesOut = 3;
                LineBuffer linebuffer = new LineBuffer();

                if (linebuffer != null)
                {
                    try
                    {
                        LineBuffer linebuffer1 = LineBuffer.readAll(new InputStreamReader(inputstream));
                        linebuffer1 = ShaderPackParser.resolveIncludes(linebuffer1, filename, shaderPack, 0, list, 0);
                        MacroState macrostate = new MacroState();

                        for (String s : linebuffer1)
                        {
                            s = applyOptions(s, ashaderoption);
                            linebuffer.add(s);

                            if (macrostate.processLine(s))
                            {
                                ShaderLine shaderline = ShaderParser.parseLine(s);

                                if (shaderline != null)
                                {
                                    if (shaderline.isExtension("GL_ARB_geometry_shader4"))
                                    {
                                        String s1 = Config.normalize(shaderline.getValue());

                                        if (s1.equals("enable") || s1.equals("require") || s1.equals("warn"))
                                        {
                                            progArbGeometryShader4 = true;
                                        }
                                    }

                                    if (shaderline.isExtension("GL_EXT_geometry_shader4"))
                                    {
                                        String s3 = Config.normalize(shaderline.getValue());

                                        if (s3.equals("enable") || s3.equals("require") || s3.equals("warn"))
                                        {
                                            progExtGeometryShader4 = true;
                                        }
                                    }

                                    if (shaderline.isConstInt("maxVerticesOut"))
                                    {
                                        progMaxVerticesOut = shaderline.getValueInt();
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception exception)
                    {
                        SMCLog.severe("Couldn't read " + filename + "!");
                        exception.printStackTrace();
                        ARBShaderObjects.glDeleteObjectARB(i);
                        return 0;
                    }
                }

                String s2 = linebuffer.toString();

                if (saveFinalShaders)
                {
                    saveShader(filename, s2);
                }

                ARBShaderObjects.glShaderSourceARB(i, (CharSequence)s2);
                ARBShaderObjects.glCompileShaderARB(i);

                if (GL43.glGetShaderi(i, 35713) != 1)
                {
                    SMCLog.severe("Error compiling geometry shader: " + filename);
                }

                printShaderLogInfo(i, filename, list);
                return i;
            }
        }
    }

    private static int createFragShader(Program program, String filename)
    {
        InputStream inputstream = shaderPack.getResourceAsStream(filename);

        if (inputstream == null)
        {
            return 0;
        }
        else
        {
            int i = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

            if (i == 0)
            {
                return 0;
            }
            else
            {
                ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
                List<String> list = new ArrayList<>();
                LineBuffer linebuffer = new LineBuffer();

                if (linebuffer != null)
                {
                    try
                    {
                        LineBuffer linebuffer1 = LineBuffer.readAll(new InputStreamReader(inputstream));
                        linebuffer1 = ShaderPackParser.resolveIncludes(linebuffer1, filename, shaderPack, 0, list, 0);
                        MacroState macrostate = new MacroState();

                        for (String s : linebuffer1)
                        {
                            s = applyOptions(s, ashaderoption);
                            linebuffer.add(s);

                            if (macrostate.processLine(s))
                            {
                                ShaderLine shaderline = ShaderParser.parseLine(s);

                                if (shaderline != null)
                                {
                                    if (shaderline.isUniform())
                                    {
                                        String s9 = shaderline.getName();
                                        int l1;

                                        if ((l1 = ShaderParser.getShadowDepthIndex(s9)) >= 0)
                                        {
                                            usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, l1 + 1);
                                        }
                                        else if ((l1 = ShaderParser.getShadowColorIndex(s9)) >= 0)
                                        {
                                            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, l1 + 1);
                                        }
                                        else if ((l1 = ShaderParser.getShadowColorImageIndex(s9)) >= 0)
                                        {
                                            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, l1 + 1);
                                            bindImageTextures = true;
                                        }
                                        else if ((l1 = ShaderParser.getDepthIndex(s9)) >= 0)
                                        {
                                            usedDepthBuffers = Math.max(usedDepthBuffers, l1 + 1);
                                        }
                                        else if (s9.equals("gdepth") && gbuffersFormat[1] == 6408)
                                        {
                                            gbuffersFormat[1] = 34836;
                                        }
                                        else if ((l1 = ShaderParser.getColorIndex(s9)) >= 0)
                                        {
                                            usedColorBuffers = Math.max(usedColorBuffers, l1 + 1);
                                        }
                                        else if ((l1 = ShaderParser.getColorImageIndex(s9)) >= 0)
                                        {
                                            usedColorBuffers = Math.max(usedColorBuffers, l1 + 1);
                                            bindImageTextures = true;
                                        }
                                        else if (s9.equals("centerDepthSmooth"))
                                        {
                                            centerDepthSmoothEnabled = true;
                                        }
                                    }
                                    else if (!shaderline.isConstInt("shadowMapResolution") && !shaderline.isProperty("SHADOWRES"))
                                    {
                                        if (!shaderline.isConstFloat("shadowMapFov") && !shaderline.isProperty("SHADOWFOV"))
                                        {
                                            if (!shaderline.isConstFloat("shadowDistance") && !shaderline.isProperty("SHADOWHPL"))
                                            {
                                                if (shaderline.isConstFloat("shadowDistanceRenderMul"))
                                                {
                                                    shadowDistanceRenderMul = shaderline.getValueFloat();
                                                    SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                                                }
                                                else if (shaderline.isConstFloat("shadowIntervalSize"))
                                                {
                                                    shadowIntervalSize = shaderline.getValueFloat();
                                                    SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                                                }
                                                else if (shaderline.isConstBool("generateShadowMipmap", true))
                                                {
                                                    Arrays.fill(shadowMipmapEnabled, true);
                                                    SMCLog.info("Generate shadow mipmap");
                                                }
                                                else if (shaderline.isConstBool("generateShadowColorMipmap", true))
                                                {
                                                    Arrays.fill(shadowColorMipmapEnabled, true);
                                                    SMCLog.info("Generate shadow color mipmap");
                                                }
                                                else if (shaderline.isConstBool("shadowHardwareFiltering", true))
                                                {
                                                    Arrays.fill(shadowHardwareFilteringEnabled, true);
                                                    SMCLog.info("Hardware shadow filtering enabled.");
                                                }
                                                else if (shaderline.isConstBool("shadowHardwareFiltering0", true))
                                                {
                                                    shadowHardwareFilteringEnabled[0] = true;
                                                    SMCLog.info("shadowHardwareFiltering0");
                                                }
                                                else if (shaderline.isConstBool("shadowHardwareFiltering1", true))
                                                {
                                                    shadowHardwareFilteringEnabled[1] = true;
                                                    SMCLog.info("shadowHardwareFiltering1");
                                                }
                                                else if (shaderline.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true))
                                                {
                                                    shadowMipmapEnabled[0] = true;
                                                    SMCLog.info("shadowtex0Mipmap");
                                                }
                                                else if (shaderline.isConstBool("shadowtex1Mipmap", true))
                                                {
                                                    shadowMipmapEnabled[1] = true;
                                                    SMCLog.info("shadowtex1Mipmap");
                                                }
                                                else if (shaderline.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true))
                                                {
                                                    shadowColorMipmapEnabled[0] = true;
                                                    SMCLog.info("shadowcolor0Mipmap");
                                                }
                                                else if (shaderline.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true))
                                                {
                                                    shadowColorMipmapEnabled[1] = true;
                                                    SMCLog.info("shadowcolor1Mipmap");
                                                }
                                                else if (shaderline.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true))
                                                {
                                                    shadowFilterNearest[0] = true;
                                                    SMCLog.info("shadowtex0Nearest");
                                                }
                                                else if (shaderline.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true))
                                                {
                                                    shadowFilterNearest[1] = true;
                                                    SMCLog.info("shadowtex1Nearest");
                                                }
                                                else if (shaderline.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true))
                                                {
                                                    shadowColorFilterNearest[0] = true;
                                                    SMCLog.info("shadowcolor0Nearest");
                                                }
                                                else if (shaderline.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true))
                                                {
                                                    shadowColorFilterNearest[1] = true;
                                                    SMCLog.info("shadowcolor1Nearest");
                                                }
                                                else if (!shaderline.isConstFloat("wetnessHalflife") && !shaderline.isProperty("WETNESSHL"))
                                                {
                                                    if (!shaderline.isConstFloat("drynessHalflife") && !shaderline.isProperty("DRYNESSHL"))
                                                    {
                                                        if (shaderline.isConstFloat("eyeBrightnessHalflife"))
                                                        {
                                                            eyeBrightnessHalflife = shaderline.getValueFloat();
                                                            SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                                                        }
                                                        else if (shaderline.isConstFloat("centerDepthHalflife"))
                                                        {
                                                            centerDepthSmoothHalflife = shaderline.getValueFloat();
                                                            SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                                                        }
                                                        else if (shaderline.isConstFloat("sunPathRotation"))
                                                        {
                                                            sunPathRotation = shaderline.getValueFloat();
                                                            SMCLog.info("Sun path rotation: " + sunPathRotation);
                                                        }
                                                        else if (shaderline.isConstFloat("ambientOcclusionLevel"))
                                                        {
                                                            aoLevel = Config.limit(shaderline.getValueFloat(), 0.0F, 1.0F);
                                                            SMCLog.info("AO Level: " + aoLevel);
                                                        }
                                                        else if (shaderline.isConstInt("superSamplingLevel"))
                                                        {
                                                            int j = shaderline.getValueInt();

                                                            if (j > 1)
                                                            {
                                                                SMCLog.info("Super sampling level: " + j + "x");
                                                                superSamplingLevel = j;
                                                            }
                                                            else
                                                            {
                                                                superSamplingLevel = 1;
                                                            }
                                                        }
                                                        else if (shaderline.isConstInt("noiseTextureResolution"))
                                                        {
                                                            noiseTextureResolution = shaderline.getValueInt();
                                                            noiseTextureEnabled = true;
                                                            SMCLog.info("Noise texture enabled");
                                                            SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                                                        }
                                                        else if (shaderline.isConstIntSuffix("Format"))
                                                        {
                                                            String s3 = StrUtils.removeSuffix(shaderline.getName(), "Format");
                                                            String s1 = shaderline.getValue();
                                                            int k = getTextureFormatFromString(s1);

                                                            if (k != 0)
                                                            {
                                                                int l = getBufferIndex(s3);

                                                                if (l >= 0)
                                                                {
                                                                    gbuffersFormat[l] = k;
                                                                    SMCLog.info("%s format: %s", s3, s1);
                                                                }

                                                                int i1 = ShaderParser.getShadowColorIndex(s3);

                                                                if (i1 >= 0)
                                                                {
                                                                    shadowBuffersFormat[i1] = k;
                                                                    SMCLog.info("%s format: %s", s3, s1);
                                                                }
                                                            }
                                                        }
                                                        else if (shaderline.isConstBoolSuffix("Clear", false))
                                                        {
                                                            if (program.getProgramStage().isAnyComposite())
                                                            {
                                                                String s4 = StrUtils.removeSuffix(shaderline.getName(), "Clear");
                                                                int j1 = getBufferIndex(s4);

                                                                if (j1 >= 0)
                                                                {
                                                                    gbuffersClear[j1] = false;
                                                                    SMCLog.info("%s clear disabled", s4);
                                                                }

                                                                int i2 = ShaderParser.getShadowColorIndex(s4);

                                                                if (i2 >= 0)
                                                                {
                                                                    shadowBuffersClear[i2] = false;
                                                                    SMCLog.info("%s clear disabled", s4);
                                                                }
                                                            }
                                                        }
                                                        else if (shaderline.isConstVec4Suffix("ClearColor"))
                                                        {
                                                            if (program.getProgramStage().isAnyComposite())
                                                            {
                                                                String s5 = StrUtils.removeSuffix(shaderline.getName(), "ClearColor");
                                                                Vector4f vector4f = shaderline.getValueVec4();

                                                                if (vector4f != null)
                                                                {
                                                                    int j2 = getBufferIndex(s5);

                                                                    if (j2 >= 0)
                                                                    {
                                                                        gbuffersClearColor[j2] = vector4f;
                                                                        SMCLog.info("%s clear color: %s %s %s %s", s5, vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                                                                    }

                                                                    int l2 = ShaderParser.getShadowColorIndex(s5);

                                                                    if (l2 >= 0)
                                                                    {
                                                                        shadowBuffersClearColor[l2] = vector4f;
                                                                        SMCLog.info("%s clear color: %s %s %s %s", s5, vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    SMCLog.warning("Invalid color value: " + shaderline.getValue());
                                                                }
                                                            }
                                                        }
                                                        else if (shaderline.isProperty("GAUX4FORMAT", "RGBA32F"))
                                                        {
                                                            gbuffersFormat[7] = 34836;
                                                            SMCLog.info("gaux4 format : RGB32AF");
                                                        }
                                                        else if (shaderline.isProperty("GAUX4FORMAT", "RGB32F"))
                                                        {
                                                            gbuffersFormat[7] = 34837;
                                                            SMCLog.info("gaux4 format : RGB32F");
                                                        }
                                                        else if (shaderline.isProperty("GAUX4FORMAT", "RGB16"))
                                                        {
                                                            gbuffersFormat[7] = 32852;
                                                            SMCLog.info("gaux4 format : RGB16");
                                                        }
                                                        else if (shaderline.isConstBoolSuffix("MipmapEnabled", true))
                                                        {
                                                            if (program.getProgramStage().isAnyComposite())
                                                            {
                                                                String s6 = StrUtils.removeSuffix(shaderline.getName(), "MipmapEnabled");
                                                                int k1 = getBufferIndex(s6);

                                                                if (k1 >= 0)
                                                                {
                                                                    int k2 = program.getCompositeMipmapSetting();
                                                                    k2 = k2 | 1 << k1;
                                                                    program.setCompositeMipmapSetting(k2);
                                                                    SMCLog.info("%s mipmap enabled", s6);
                                                                }
                                                            }
                                                        }
                                                        else if (shaderline.isProperty("DRAWBUFFERS"))
                                                        {
                                                            String s7 = shaderline.getValue();
                                                            String[] astring = ShaderParser.parseDrawBuffers(s7);

                                                            if (astring != null)
                                                            {
                                                                program.setDrawBufSettings(astring);
                                                            }
                                                            else
                                                            {
                                                                SMCLog.warning("Invalid draw buffers: " + s7);
                                                            }
                                                        }
                                                        else if (shaderline.isProperty("RENDERTARGETS"))
                                                        {
                                                            String s8 = shaderline.getValue();
                                                            String[] astring1 = ShaderParser.parseRenderTargets(s8);

                                                            if (astring1 != null)
                                                            {
                                                                program.setDrawBufSettings(astring1);
                                                            }
                                                            else
                                                            {
                                                                SMCLog.warning("Invalid render targets: " + s8);
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        drynessHalfLife = shaderline.getValueFloat();
                                                        SMCLog.info("Dryness halflife: " + drynessHalfLife);
                                                    }
                                                }
                                                else
                                                {
                                                    wetnessHalfLife = shaderline.getValueFloat();
                                                    SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                                                }
                                            }
                                            else
                                            {
                                                shadowMapHalfPlane = shaderline.getValueFloat();
                                                shadowMapIsOrtho = true;
                                                SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                                            }
                                        }
                                        else
                                        {
                                            shadowMapFOV = shaderline.getValueFloat();
                                            shadowMapIsOrtho = false;
                                            SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                                        }
                                    }
                                    else
                                    {
                                        spShadowMapWidth = spShadowMapHeight = shaderline.getValueInt();
                                        shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                                        SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception exception)
                    {
                        SMCLog.severe("Couldn't read " + filename + "!");
                        exception.printStackTrace();
                        ARBShaderObjects.glDeleteObjectARB(i);
                        return 0;
                    }
                }

                String s2 = linebuffer.toString();

                if (saveFinalShaders)
                {
                    saveShader(filename, s2);
                }

                ARBShaderObjects.glShaderSourceARB(i, (CharSequence)s2);
                ARBShaderObjects.glCompileShaderARB(i);

                if (GL43.glGetShaderi(i, 35713) != 1)
                {
                    SMCLog.severe("Error compiling fragment shader: " + filename);
                }

                printShaderLogInfo(i, filename, list);
                return i;
            }
        }
    }

    public static void saveShader(String filename, String code)
    {
        try
        {
            File file1 = new File(shaderPacksDir, "debug/" + filename);
            file1.getParentFile().mkdirs();
            Config.writeFile(file1, code);
        }
        catch (IOException ioexception)
        {
            Config.warn("Error saving: " + filename);
            ioexception.printStackTrace();
        }
    }

    private static void clearDirectory(File dir)
    {
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                File[] afile = dir.listFiles();

                if (afile != null)
                {
                    for (int i = 0; i < afile.length; ++i)
                    {
                        File file1 = afile[i];

                        if (file1.isDirectory())
                        {
                            clearDirectory(file1);
                        }

                        file1.delete();
                    }
                }
            }
        }
    }

    private static boolean printLogInfo(int obj, String name)
    {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterivARB(obj, 35716, intbuffer);
        int i = intbuffer.get();

        if (i > 1)
        {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(i);
            ((Buffer)intbuffer).flip();
            ARBShaderObjects.glGetInfoLogARB(obj, intbuffer, bytebuffer);
            byte[] abyte = new byte[i];
            bytebuffer.get(abyte);

            if (abyte[i - 1] == 0)
            {
                abyte[i - 1] = 10;
            }

            String s = new String(abyte, StandardCharsets.US_ASCII);
            s = StrUtils.trim(s, " \n\r\t");
            SMCLog.info("Info log: " + name + "\n" + s);
            return false;
        }
        else
        {
            return true;
        }
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles)
    {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        int i = GL43.glGetShaderi(shader, 35716);

        if (i <= 1)
        {
            return true;
        }
        else
        {
            for (int j = 0; j < listFiles.size(); ++j)
            {
                String s = listFiles.get(j);
                SMCLog.info("File: " + (j + 1) + " = " + s);
            }

            String s1 = GL43.glGetShaderInfoLog(shader, i);
            s1 = StrUtils.trim(s1, " \n\r\t");
            SMCLog.info("Shader info log: " + name + "\n" + s1);
            return false;
        }
    }

    public static void useProgram(Program program)
    {
        checkGLError("pre-useProgram");

        if (isShadowPass)
        {
            program = ProgramShadow;
        }
        else if (isEntitiesGlowing)
        {
            program = ProgramEntitiesGlowing;
        }

        if (activeProgram != program)
        {
            flushRenderBuffers();
            updateAlphaBlend(activeProgram, program);

            if (glDebugGroups && glDebugGroupProgram)
            {
                KHRDebug.glPopDebugGroup();
            }

            activeProgram = program;

            if (glDebugGroups)
            {
                KHRDebug.glPushDebugGroup(33354, 0, activeProgram.getRealProgramName());
                glDebugGroupProgram = true;
            }

            int i = program.getId();
            activeProgramID = i;
            ARBShaderObjects.glUseProgramObjectARB(i);

            if (checkGLError("useProgram") != 0)
            {
                program.setId(0);
                i = program.getId();
                activeProgramID = i;
                ARBShaderObjects.glUseProgramObjectARB(i);
            }

            shaderUniforms.setProgram(i);

            if (customUniforms != null)
            {
                customUniforms.setProgram(i);
            }

            if (i != 0)
            {
                DrawBuffers drawbuffers = program.getDrawBuffers();

                if (isRenderingDfb)
                {
                    GlState.setDrawBuffers(drawbuffers);
                }

                setProgramUniforms(program.getProgramStage());
                setImageUniforms();
                checkGLError("end useProgram");
            }
        }
    }

    private static void setProgramUniforms(ProgramStage programStage)
    {
        switch (programStage)
        {
            case GBUFFERS:
                setProgramUniform1i(uniform_texture, 0);
                setProgramUniform1i(uniform_lightmap, 2);
                setProgramUniform1i(uniform_normals, 1);
                setProgramUniform1i(uniform_specular, 3);
                setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(uniform_watershadow, 4);
                setProgramUniform1i(uniform_shadowtex0, 4);
                setProgramUniform1i(uniform_shadowtex1, 5);
                setProgramUniform1i(uniform_depthtex0, 6);

                if (customTexturesGbuffers != null || hasDeferredPrograms)
                {
                    setProgramUniform1i(uniform_gaux1, 7);
                    setProgramUniform1i(uniform_gaux2, 8);
                    setProgramUniform1i(uniform_gaux3, 9);
                    setProgramUniform1i(uniform_gaux4, 10);
                    setProgramUniform1i(uniform_colortex4, 7);
                    setProgramUniform1i(uniform_colortex5, 8);
                    setProgramUniform1i(uniform_colortex6, 9);
                    setProgramUniform1i(uniform_colortex7, 10);

                    if (usedColorBuffers > 8)
                    {
                        setProgramUniform1i(uniform_colortex8, 16);
                        setProgramUniform1i(uniform_colortex9, 17);
                        setProgramUniform1i(uniform_colortex10, 18);
                        setProgramUniform1i(uniform_colortex11, 19);
                        setProgramUniform1i(uniform_colortex12, 20);
                        setProgramUniform1i(uniform_colortex13, 21);
                        setProgramUniform1i(uniform_colortex14, 22);
                        setProgramUniform1i(uniform_colortex15, 23);
                    }
                }

                setProgramUniform1i(uniform_depthtex1, 11);
                setProgramUniform1i(uniform_shadowcolor, 13);
                setProgramUniform1i(uniform_shadowcolor0, 13);
                setProgramUniform1i(uniform_shadowcolor1, 14);
                setProgramUniform1i(uniform_noisetex, 15);
                break;

            case SHADOWCOMP:
            case PREPARE:
            case DEFERRED:
            case COMPOSITE:
                setProgramUniform1i(uniform_gcolor, 0);
                setProgramUniform1i(uniform_gdepth, 1);
                setProgramUniform1i(uniform_gnormal, 2);
                setProgramUniform1i(uniform_composite, 3);
                setProgramUniform1i(uniform_gaux1, 7);
                setProgramUniform1i(uniform_gaux2, 8);
                setProgramUniform1i(uniform_gaux3, 9);
                setProgramUniform1i(uniform_gaux4, 10);
                setProgramUniform1i(uniform_colortex0, 0);
                setProgramUniform1i(uniform_colortex1, 1);
                setProgramUniform1i(uniform_colortex2, 2);
                setProgramUniform1i(uniform_colortex3, 3);
                setProgramUniform1i(uniform_colortex4, 7);
                setProgramUniform1i(uniform_colortex5, 8);
                setProgramUniform1i(uniform_colortex6, 9);
                setProgramUniform1i(uniform_colortex7, 10);

                if (usedColorBuffers > 8)
                {
                    setProgramUniform1i(uniform_colortex8, 16);
                    setProgramUniform1i(uniform_colortex9, 17);
                    setProgramUniform1i(uniform_colortex10, 18);
                    setProgramUniform1i(uniform_colortex11, 19);
                    setProgramUniform1i(uniform_colortex12, 20);
                    setProgramUniform1i(uniform_colortex13, 21);
                    setProgramUniform1i(uniform_colortex14, 22);
                    setProgramUniform1i(uniform_colortex15, 23);
                }

                setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(uniform_watershadow, 4);
                setProgramUniform1i(uniform_shadowtex0, 4);
                setProgramUniform1i(uniform_shadowtex1, 5);
                setProgramUniform1i(uniform_gdepthtex, 6);
                setProgramUniform1i(uniform_depthtex0, 6);
                setProgramUniform1i(uniform_depthtex1, 11);
                setProgramUniform1i(uniform_depthtex2, 12);
                setProgramUniform1i(uniform_shadowcolor, 13);
                setProgramUniform1i(uniform_shadowcolor0, 13);
                setProgramUniform1i(uniform_shadowcolor1, 14);
                setProgramUniform1i(uniform_noisetex, 15);
                break;

            case SHADOW:
                setProgramUniform1i(uniform_tex, 0);
                setProgramUniform1i(uniform_texture, 0);
                setProgramUniform1i(uniform_lightmap, 2);
                setProgramUniform1i(uniform_normals, 1);
                setProgramUniform1i(uniform_specular, 3);
                setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(uniform_watershadow, 4);
                setProgramUniform1i(uniform_shadowtex0, 4);
                setProgramUniform1i(uniform_shadowtex1, 5);

                if (customTexturesGbuffers != null)
                {
                    setProgramUniform1i(uniform_gaux1, 7);
                    setProgramUniform1i(uniform_gaux2, 8);
                    setProgramUniform1i(uniform_gaux3, 9);
                    setProgramUniform1i(uniform_gaux4, 10);
                    setProgramUniform1i(uniform_colortex4, 7);
                    setProgramUniform1i(uniform_colortex5, 8);
                    setProgramUniform1i(uniform_colortex6, 9);
                    setProgramUniform1i(uniform_colortex7, 10);

                    if (usedColorBuffers > 8)
                    {
                        setProgramUniform1i(uniform_colortex8, 16);
                        setProgramUniform1i(uniform_colortex9, 17);
                        setProgramUniform1i(uniform_colortex10, 18);
                        setProgramUniform1i(uniform_colortex11, 19);
                        setProgramUniform1i(uniform_colortex12, 20);
                        setProgramUniform1i(uniform_colortex13, 21);
                        setProgramUniform1i(uniform_colortex14, 22);
                        setProgramUniform1i(uniform_colortex15, 23);
                    }
                }

                setProgramUniform1i(uniform_shadowcolor, 13);
                setProgramUniform1i(uniform_shadowcolor0, 13);
                setProgramUniform1i(uniform_shadowcolor1, 14);
                setProgramUniform1i(uniform_noisetex, 15);
        }

        ItemStack itemstack = mc.player != null ? mc.player.getHeldItemMainhand() : null;
        Item item = itemstack != null ? itemstack.getItem() : null;
        int i = -1;
        Block block = null;

        if (item != null)
        {
            i = Registry.ITEM.getId(item);

            if (item instanceof BlockItem)
            {
                block = ((BlockItem)item).getBlock();
            }

            i = ItemAliases.getItemAliasId(i);
        }

        int j = block != null ? block.getDefaultState().getLightValue() : 0;
        ItemStack itemstack1 = mc.player != null ? mc.player.getHeldItemOffhand() : null;
        Item item1 = itemstack1 != null ? itemstack1.getItem() : null;
        int k = -1;
        Block block1 = null;

        if (item1 != null)
        {
            k = Registry.ITEM.getId(item1);

            if (item1 instanceof BlockItem)
            {
                block1 = ((BlockItem)item1).getBlock();
            }

            k = ItemAliases.getItemAliasId(k);
        }

        int l = block1 != null ? block1.getDefaultState().getLightValue() : 0;

        if (isOldHandLight() && l > j)
        {
            i = k;
            j = l;
        }

        float f = mc.player != null ? mc.player.getDarknessAmbience() : 0.0F;
        setProgramUniform1i(uniform_heldItemId, i);
        setProgramUniform1i(uniform_heldBlockLightValue, j);
        setProgramUniform1i(uniform_heldItemId2, k);
        setProgramUniform1i(uniform_heldBlockLightValue2, l);
        setProgramUniform1i(uniform_fogMode, fogEnabled ? fogMode : 0);
        setProgramUniform1f(uniform_fogDensity, fogEnabled ? fogDensity : 0.0F);
        setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
        setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
        setProgramUniform1i(uniform_worldTime, (int)(worldTime % 24000L));
        setProgramUniform1i(uniform_worldDay, (int)(worldTime / 24000L));
        setProgramUniform1i(uniform_moonPhase, moonPhase);
        setProgramUniform1i(uniform_frameCounter, frameCounter);
        setProgramUniform1f(uniform_frameTime, frameTime);
        setProgramUniform1f(uniform_frameTimeCounter, frameTimeCounter);
        setProgramUniform1f(uniform_sunAngle, sunAngle);
        setProgramUniform1f(uniform_shadowAngle, shadowAngle);
        setProgramUniform1f(uniform_rainStrength, rainStrength);
        setProgramUniform1f(uniform_aspectRatio, (float)renderWidth / (float)renderHeight);
        setProgramUniform1f(uniform_viewWidth, (float)renderWidth);
        setProgramUniform1f(uniform_viewHeight, (float)renderHeight);
        setProgramUniform1f(uniform_near, 0.05F);
        setProgramUniform1f(uniform_far, (float)(mc.gameSettings.renderDistanceChunks * 16));
        setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
        setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
        setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
        setProgramUniform3f(uniform_previousCameraPosition, (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
        setProgramUniform3f(uniform_cameraPosition, (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
        setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
        setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
        setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
        setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);

        if (hasShadowMap)
        {
            setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
            setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
            setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
            setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
        }

        setProgramUniform1f(uniform_wetness, wetness);
        setProgramUniform1f(uniform_eyeAltitude, eyePosY);
        setProgramUniform2i(uniform_eyeBrightness, eyeBrightness & 65535, eyeBrightness >> 16);
        setProgramUniform2i(uniform_eyeBrightnessSmooth, Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
        setProgramUniform2i(uniform_terrainTextureSize, terrainTextureSize[0], terrainTextureSize[1]);
        setProgramUniform1i(uniform_terrainIconSize, terrainIconSize);
        setProgramUniform1i(uniform_isEyeInWater, isEyeInWater);
        setProgramUniform1f(uniform_nightVision, nightVision);
        setProgramUniform1f(uniform_blindness, blindness);
        setProgramUniform1f(uniform_screenBrightness, (float)mc.gameSettings.gamma);
        setProgramUniform1i(uniform_hideGUI, mc.gameSettings.hideGUI ? 1 : 0);
        setProgramUniform1f(uniform_centerDepthSmooth, centerDepthSmooth);
        setProgramUniform2i(uniform_atlasSize, atlasSizeX, atlasSizeY);
        setProgramUniform1f(uniform_playerMood, f);
        setProgramUniform1i(uniform_renderStage, renderStage.ordinal());

        if (customUniforms != null)
        {
            customUniforms.update();
        }
    }

    private static void setImageUniforms()
    {
        if (bindImageTextures)
        {
            uniform_colorimg0.setValue(colorImageUnit[0]);
            uniform_colorimg1.setValue(colorImageUnit[1]);
            uniform_colorimg2.setValue(colorImageUnit[2]);
            uniform_colorimg3.setValue(colorImageUnit[3]);
            uniform_colorimg4.setValue(colorImageUnit[4]);
            uniform_colorimg5.setValue(colorImageUnit[5]);
            uniform_shadowcolorimg0.setValue(shadowColorImageUnit[0]);
            uniform_shadowcolorimg1.setValue(shadowColorImageUnit[1]);
        }
    }

    private static void updateAlphaBlend(Program programOld, Program programNew)
    {
        if (programOld.getAlphaState() != null)
        {
            GlStateManager.unlockAlpha();
        }

        if (programOld.getBlendState() != null)
        {
            GlStateManager.unlockBlend();
        }

        if (programOld.getBlendStatesIndexed() != null)
        {
            GlStateManager.applyCurrentBlend();
        }

        GlAlphaState glalphastate = programNew.getAlphaState();

        if (glalphastate != null)
        {
            GlStateManager.lockAlpha(glalphastate);
        }

        GlBlendState glblendstate = programNew.getBlendState();

        if (glblendstate != null)
        {
            GlStateManager.lockBlend(glblendstate);
        }

        if (programNew.getBlendStatesIndexed() != null)
        {
            GlStateManager.setBlendsIndexed(programNew.getBlendStatesIndexed());
        }
    }

    private static void setProgramUniform1i(ShaderUniform1i su, int value)
    {
        su.setValue(value);
    }

    private static void setProgramUniform2i(ShaderUniform2i su, int i0, int i1)
    {
        su.setValue(i0, i1);
    }

    private static void setProgramUniform1f(ShaderUniform1f su, float value)
    {
        su.setValue(value);
    }

    private static void setProgramUniform3f(ShaderUniform3f su, float f0, float f1, float f2)
    {
        su.setValue(f0, f1, f2);
    }

    private static void setProgramUniformMatrix4ARB(ShaderUniformM4 su, boolean transpose, FloatBuffer matrix)
    {
        su.setValue(transpose, matrix);
    }

    public static int getBufferIndex(String name)
    {
        int i = ShaderParser.getIndex(name, "colortex", 0, 15);

        if (i >= 0)
        {
            return i;
        }
        else
        {
            int j = ShaderParser.getIndex(name, "colorimg", 0, 15);

            if (j >= 0)
            {
                return j;
            }
            else if (name.equals("gcolor"))
            {
                return 0;
            }
            else if (name.equals("gdepth"))
            {
                return 1;
            }
            else if (name.equals("gnormal"))
            {
                return 2;
            }
            else if (name.equals("composite"))
            {
                return 3;
            }
            else if (name.equals("gaux1"))
            {
                return 4;
            }
            else if (name.equals("gaux2"))
            {
                return 5;
            }
            else if (name.equals("gaux3"))
            {
                return 6;
            }
            else
            {
                return name.equals("gaux4") ? 7 : -1;
            }
        }
    }

    private static int getTextureFormatFromString(String par)
    {
        par = par.trim();

        for (int i = 0; i < formatNames.length; ++i)
        {
            String s = formatNames[i];

            if (par.equals(s))
            {
                return formatIds[i];
            }
        }

        return 0;
    }

    public static int getImageFormat(int textureFormat)
    {
        switch (textureFormat)
        {
            case 6407:
                return 32849;

            case 6408:
                return 32856;

            case 8194:
                return 33321;

            case 10768:
                return 32849;

            case 32855:
                return 32856;

            case 33319:
                return 33323;

            case 35901:
                return 32852;

            default:
                return textureFormat;
        }
    }

    private static void setupNoiseTexture()
    {
        if (noiseTexture == null && noiseTexturePath != null)
        {
            noiseTexture = loadCustomTexture(15, noiseTexturePath);
        }

        if (noiseTexture == null)
        {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap()
    {
        mapBlockToEntityData = new IdentityHashMap<>(300);

        if (mapBlockToEntityData.isEmpty())
        {
            for (ResourceLocation resourcelocation : Registry.BLOCK.keySet())
            {
                Block block = Registry.BLOCK.getOrDefault(resourcelocation);
                int i = Registry.BLOCK.getId(block);
                mapBlockToEntityData.put(block, i);
            }
        }

        BufferedReader bufferedreader = null;

        try
        {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception exception1)
        {
        }

        if (bufferedreader != null)
        {
            String s1;

            try
            {
                while ((s1 = bufferedreader.readLine()) != null)
                {
                    Matcher matcher = patternLoadEntityDataMap.matcher(s1);

                    if (matcher.matches())
                    {
                        String s2 = matcher.group(1);
                        String s = matcher.group(2);
                        int j = Integer.parseInt(s);
                        ResourceLocation resourcelocation1 = new ResourceLocation(s2);

                        if (Registry.BLOCK.containsKey(resourcelocation1))
                        {
                            Block block1 = Registry.BLOCK.getOrDefault(resourcelocation1);
                            mapBlockToEntityData.put(block1, j);
                        }
                        else
                        {
                            SMCLog.warning("Unknown block name %s", s2);
                        }
                    }
                    else
                    {
                        SMCLog.warning("unmatched %s\n", s1);
                    }
                }
            }
            catch (Exception exception2)
            {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }

        if (bufferedreader != null)
        {
            try
            {
                bufferedreader.close();
            }
            catch (Exception exception)
            {
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf)
    {
        int i = buf.limit();

        for (int j = buf.position(); j < i; ++j)
        {
            buf.put(j, 0);
        }

        return buf;
    }

    private static DrawBuffers fillIntBufferZero(DrawBuffers buf)
    {
        int i = buf.limit();

        for (int j = buf.position(); j < i; ++j)
        {
            buf.put(j, 0);
        }

        return buf;
    }

    public static void uninit()
    {
        if (isShaderPackInitialized)
        {
            checkGLError("Shaders.uninit pre");

            for (int i = 0; i < ProgramsAll.length; ++i)
            {
                Program program = ProgramsAll[i];

                if (program.getRef() != 0)
                {
                    ARBShaderObjects.glDeleteObjectARB(program.getRef());
                    checkGLError("del programRef");
                }

                program.setRef(0);
                program.setId(0);
                program.setDrawBufSettings((String[])null);
                program.setDrawBuffers((DrawBuffers)null);
                program.setCompositeMipmapSetting(0);
                ComputeProgram[] acomputeprogram = program.getComputePrograms();

                for (int j = 0; j < acomputeprogram.length; ++j)
                {
                    ComputeProgram computeprogram = acomputeprogram[j];

                    if (computeprogram.getRef() != 0)
                    {
                        ARBShaderObjects.glDeleteObjectARB(computeprogram.getRef());
                        checkGLError("del programRef");
                    }

                    computeprogram.setRef(0);
                    computeprogram.setId(0);
                }

                program.setComputePrograms(new ComputeProgram[0]);
            }

            hasDeferredPrograms = false;
            hasShadowcompPrograms = false;
            hasPreparePrograms = false;

            if (dfb != null)
            {
                dfb.delete();
                dfb = null;
                checkGLError("del dfb");
            }

            if (sfb != null)
            {
                sfb.delete();
                sfb = null;
                checkGLError("del sfb");
            }

            if (dfbDrawBuffers != null)
            {
                fillIntBufferZero(dfbDrawBuffers);
            }

            if (sfbDrawBuffers != null)
            {
                fillIntBufferZero(sfbDrawBuffers);
            }

            if (noiseTexture != null)
            {
                noiseTexture.deleteTexture();
                noiseTexture = null;
            }

            for (int k = 0; k < colorImageUnit.length; ++k)
            {
                GlStateManager.bindImageTexture(colorImageUnit[k], 0, 0, false, 0, 35000, 32856);
            }

            SMCLog.info("Uninit");
            hasShadowMap = false;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize()
    {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow()
    {
        needResizeShadow = true;
    }

    private static void resize()
    {
        renderDisplayWidth = mc.getMainWindow().getFramebufferWidth();
        renderDisplayHeight = mc.getMainWindow().getFramebufferHeight();
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        setupFrameBuffer();
    }

    private static void resizeShadow()
    {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        setupShadowFrameBuffer();
    }

    private static void setupFrameBuffer()
    {
        if (dfb != null)
        {
            dfb.delete();
        }

        boolean[] aboolean = ArrayUtils.newBoolean(usedDepthBuffers, true);
        boolean[] aboolean1 = new boolean[usedDepthBuffers];
        boolean[] aboolean2 = new boolean[usedColorBuffers];
        int[] aint = bindImageTextures ? colorImageUnit : null;
        dfb = new ShadersFramebuffer("dfb", renderWidth, renderHeight, usedColorBuffers, usedDepthBuffers, 8, aboolean, aboolean1, aboolean2, colorBufferSizes, gbuffersFormat, colorTextureImageUnit, depthTextureImageUnit, aint, dfbDrawBuffers);
        dfb.setup();
    }

    public static int getPixelFormat(int internalFormat)
    {
        switch (internalFormat)
        {
            case 33329:
            case 33335:
            case 36238:
            case 36239:
                return 36251;

            case 33330:
            case 33336:
            case 36220:
            case 36221:
                return 36251;

            case 33331:
            case 33337:
            case 36232:
            case 36233:
                return 36251;

            case 33332:
            case 33338:
            case 36214:
            case 36215:
                return 36251;

            case 33333:
            case 33339:
            case 36226:
            case 36227:
                return 36251;

            case 33334:
            case 33340:
            case 36208:
            case 36209:
                return 36251;

            default:
                return 32993;
        }
    }

    private static void setupShadowFrameBuffer()
    {
        if (hasShadowMap)
        {
            isShadowPass = true;

            if (sfb != null)
            {
                sfb.delete();
            }

            DynamicDimension[] adynamicdimension = new DynamicDimension[2];
            int[] aint = bindImageTextures ? shadowColorImageUnit : null;
            sfb = new ShadersFramebuffer("sfb", shadowMapWidth, shadowMapHeight, usedShadowColorBuffers, usedShadowDepthBuffers, 8, shadowFilterNearest, shadowHardwareFilteringEnabled, shadowColorFilterNearest, adynamicdimension, shadowBuffersFormat, shadowColorTextureImageUnit, shadowDepthTextureImageUnit, aint, sfbDrawBuffers);
            sfb.setup();
            isShadowPass = false;
        }
    }

    public static void beginRender(Minecraft minecraft, ActiveRenderInfo activeRenderInfo, float partialTicks, long finishTimeNano)
    {
        checkGLError("pre beginRender");
        checkWorldChanged(mc.world);
        mc = minecraft;
        mc.getProfiler().startSection("init");
        entityRenderer = mc.gameRenderer;

        if (!isShaderPackInitialized)
        {
            try
            {
                init();
            }
            catch (IllegalStateException illegalstateexception)
            {
                if (Config.normalize(illegalstateexception.getMessage()).equals("Function is not supported"))
                {
                    printChatAndLogError("[Shaders] Error: " + illegalstateexception.getMessage());
                    illegalstateexception.printStackTrace();
                    setShaderPack("OFF");
                    return;
                }
            }
        }

        if (mc.getMainWindow().getFramebufferWidth() != renderDisplayWidth || mc.getMainWindow().getFramebufferHeight() != renderDisplayHeight)
        {
            resize();
        }

        if (needResizeShadow)
        {
            resizeShadow();
        }

        ++frameCounter;

        if (frameCounter >= 720720)
        {
            frameCounter = 0;
        }

        systemTime = System.currentTimeMillis();

        if (lastSystemTime == 0L)
        {
            lastSystemTime = systemTime;
        }

        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0F;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0F;
        pointOfViewChanged = pointOfView != mc.gameSettings.getPointOfView();
        pointOfView = mc.gameSettings.getPointOfView();
        GlStateManager.pushMatrix();
        ShadersRender.updateActiveRenderInfo(activeRenderInfo, minecraft, partialTicks);
        GlStateManager.popMatrix();
        ClientWorld clientworld = mc.world;

        if (clientworld != null)
        {
            worldTime = clientworld.getDayTime();
            diffWorldTime = (worldTime - lastWorldTime) % 24000L;

            if (diffWorldTime < 0L)
            {
                diffWorldTime += 24000L;
            }

            lastWorldTime = worldTime;
            moonPhase = clientworld.getMoonPhase();
            rainStrength = clientworld.getRainStrength(partialTicks);
            float f = (float)diffSystemTime * 0.01F;
            float f1 = (float)Math.exp(Math.log(0.5D) * (double)f / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
            wetness = wetness * f1 + rainStrength * (1.0F - f1);
            Entity entity = activeRenderInfo.getRenderViewEntity();

            if (entity != null)
            {
                isSleeping = entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping();
                eyePosY = (float)activeRenderInfo.getProjectedView().getY();
                eyeBrightness = mc.getRenderManager().getPackedLight(entity, partialTicks);
                float f2 = (float)diffSystemTime * 0.01F;
                float f3 = (float)Math.exp(Math.log(0.5D) * (double)f2 / (double)eyeBrightnessHalflife);
                eyeBrightnessFadeX = eyeBrightnessFadeX * f3 + (float)(eyeBrightness & 65535) * (1.0F - f3);
                eyeBrightnessFadeY = eyeBrightnessFadeY * f3 + (float)(eyeBrightness >> 16) * (1.0F - f3);
                FluidState fluidstate = activeRenderInfo.getFluidState();

                if (fluidstate.isTagged(FluidTags.WATER))
                {
                    isEyeInWater = 1;
                }
                else if (fluidstate.isTagged(FluidTags.LAVA))
                {
                    isEyeInWater = 2;
                }
                else
                {
                    isEyeInWater = 0;
                }

                if (entity instanceof LivingEntity)
                {
                    LivingEntity livingentity = (LivingEntity)entity;
                    nightVision = 0.0F;

                    if (livingentity.isPotionActive(Effects.NIGHT_VISION))
                    {
                        GameRenderer gamerenderer = entityRenderer;
                        nightVision = GameRenderer.getNightVisionBrightness(livingentity, partialTicks);
                    }

                    blindness = 0.0F;

                    if (livingentity.isPotionActive(Effects.BLINDNESS))
                    {
                        int i = livingentity.getActivePotionEffect(Effects.BLINDNESS).getDuration();
                        blindness = Config.limit((float)i / 20.0F, 0.0F, 1.0F);
                    }
                }

                Vector3d vector3d = clientworld.getSkyColor(entity.getPosition(), partialTicks);
                vector3d = CustomColors.getWorldSkyColor(vector3d, clientworld, entity, partialTicks);
                skyColorR = (float)vector3d.x;
                skyColorG = (float)vector3d.y;
                skyColorB = (float)vector3d.z;
            }
        }

        isRenderingWorld = true;
        isCompositeRendered = false;
        isShadowPass = false;
        isHandRenderedMain = false;
        isHandRenderedOff = false;
        skipRenderHandMain = false;
        skipRenderHandOff = false;
        dfb.setColorBuffersFiltering(9729, 9729);
        bindGbuffersTextures();
        dfb.bindColorImages(true);

        if (sfb != null)
        {
            sfb.bindColorImages(true);
        }

        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        ((Buffer)previousProjection).position(0);
        ((Buffer)projection).position(0);
        previousProjection.put(projection);
        ((Buffer)previousProjection).position(0);
        ((Buffer)projection).position(0);
        ((Buffer)previousModelView).position(0);
        ((Buffer)modelView).position(0);
        previousModelView.put(modelView);
        ((Buffer)previousModelView).position(0);
        ((Buffer)modelView).position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, activeRenderInfo, 0, partialTicks, finishTimeNano);
        mc.getProfiler().endSection();
        dfb.setColorTextures(true);
        setRenderStage(RenderStage.NONE);
        checkGLError("end beginRender");
    }

    private static void bindGbuffersTextures()
    {
        bindTextures(4, customTexturesGbuffers);
    }

    private static void bindTextures(int startColorBuffer, ICustomTexture[] customTextures)
    {
        if (sfb != null)
        {
            sfb.bindColorTextures(0);
            sfb.bindDepthTextures(shadowDepthTextureImageUnit);
        }

        dfb.bindColorTextures(startColorBuffer);
        dfb.bindDepthTextures(depthTextureImageUnit);

        if (noiseTextureEnabled)
        {
            GlStateManager.activeTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
            GlStateManager.activeTexture(33984);
        }

        bindCustomTextures(customTextures);
    }

    public static void checkWorldChanged(ClientWorld worldin)
    {
        if (currentWorld != worldin)
        {
            World world = currentWorld;
            currentWorld = worldin;

            if (currentWorld == null)
            {
                cameraPositionX = 0.0D;
                cameraPositionY = 0.0D;
                cameraPositionZ = 0.0D;
                previousCameraPositionX = 0.0D;
                previousCameraPositionY = 0.0D;
                previousCameraPositionZ = 0.0D;
            }

            setCameraOffset(mc.getRenderViewEntity());
            int i = WorldUtils.getDimensionId(world);
            int j = WorldUtils.getDimensionId(worldin);

            if (j != i)
            {
                boolean flag = shaderPackDimensions.contains(i);
                boolean flag1 = shaderPackDimensions.contains(j);

                if (flag || flag1)
                {
                    uninit();
                }
            }

            Smoother.resetValues();
        }
    }

    public static void beginRenderPass(float partialTicks, long finishTimeNano)
    {
        if (!isShadowPass)
        {
            dfb.bindFramebuffer();
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            GlState.setDrawBuffers((DrawBuffers)null);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            useProgram(ProgramTextured);
            checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx, int vy, int vw, int vh)
    {
        GlStateManager.colorMask(true, true, true, true);

        if (isShadowPass)
        {
            GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        }
        else
        {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            dfb.bindFramebuffer();
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepthTest();
            GlState.setDrawBuffers(drawBuffersNone);
            useProgram(ProgramTextured);
            checkGLError("beginRenderPass");
        }
    }

    public static void setFogMode(int value)
    {
        fogMode = value;

        if (fogEnabled)
        {
            setProgramUniform1i(uniform_fogMode, value);
        }
    }

    public static void setFogColor(float r, float g, float b)
    {
        fogColorR = r;
        fogColorG = g;
        fogColorB = b;
        setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
    }

    public static void setClearColor(float red, float green, float blue, float alpha)
    {
        clearColor.set(red, green, blue, 1.0F);
    }

    public static void clearRenderBuffer()
    {
        if (isShadowPass)
        {
            checkGLError("shadow clear pre");
            sfb.clearDepthBuffer(new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
            checkGLError("shadow clear");
        }
        else
        {
            checkGLError("clear pre");
            Vector4f[] avector4f = new Vector4f[usedColorBuffers];

            for (int i = 0; i < avector4f.length; ++i)
            {
                avector4f[i] = getBufferClearColor(i);
            }

            dfb.clearColorBuffers(gbuffersClear, avector4f);
            dfb.setDrawBuffers();
            checkFramebufferStatus("clear");
            checkGLError("clear");
        }
    }

    public static void renderPrepare()
    {
        if (hasPreparePrograms)
        {
            renderPrepareComposites();
            bindGbuffersTextures();
            dfb.setDrawBuffers();
            dfb.setColorTextures(true);
        }
    }

    private static Vector4f getBufferClearColor(int buffer)
    {
        Vector4f vector4f = gbuffersClearColor[buffer];

        if (vector4f != null)
        {
            return vector4f;
        }
        else if (buffer == 0)
        {
            return clearColor;
        }
        else
        {
            return buffer == 1 ? CLEAR_COLOR_1 : CLEAR_COLOR_0;
        }
    }

    public static void setCamera(MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfo, float partialTicks)
    {
        Entity entity = activeRenderInfo.getRenderViewEntity();
        Vector3d vector3d = activeRenderInfo.getProjectedView();
        double d0 = vector3d.x;
        double d1 = vector3d.y;
        double d2 = vector3d.z;
        updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        updateProjectionMatrix();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        Matrix4f matrix4f1 = new Matrix4f(matrix4f);
        matrix4f1.transpose();
        matrix4f1.write(tempMat);
        ((Buffer)modelView).position(0);
        modelView.put(tempMat);
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)modelViewInverse).position(0), (FloatBuffer)((Buffer)modelView).position(0), faModelViewInverse, faModelView);
        ((Buffer)modelView).position(0);
        ((Buffer)modelViewInverse).position(0);
        checkGLError("setCamera");
    }

    public static void updateProjectionMatrix()
    {
        GL43.glGetFloatv(2983, (FloatBuffer)((Buffer)projection).position(0));
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)projectionInverse).position(0), (FloatBuffer)((Buffer)projection).position(0), faProjectionInverse, faProjection);
        ((Buffer)projection).position(0);
        ((Buffer)projectionInverse).position(0);
    }

    private static void updateShadowProjectionMatrix()
    {
        GL43.glGetFloatv(2983, (FloatBuffer)((Buffer)shadowProjection).position(0));
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)shadowProjectionInverse).position(0), (FloatBuffer)((Buffer)shadowProjection).position(0), faShadowProjectionInverse, faShadowProjection);
        ((Buffer)shadowProjection).position(0);
        ((Buffer)shadowProjectionInverse).position(0);
    }

    private static void updateCameraOffset(Entity viewEntity)
    {
        double d0 = Math.abs(cameraPositionX - previousCameraPositionX);
        double d1 = Math.abs(cameraPositionZ - previousCameraPositionZ);
        double d2 = Math.abs(cameraPositionX);
        double d3 = Math.abs(cameraPositionZ);

        if (d0 > 1000.0D || d1 > 1000.0D || d2 > 1000000.0D || d3 > 1000000.0D)
        {
            setCameraOffset(viewEntity);
        }
    }

    private static void setCameraOffset(Entity viewEntity)
    {
        if (viewEntity == null)
        {
            cameraOffsetX = 0;
            cameraOffsetZ = 0;
        }
        else
        {
            cameraOffsetX = (int)viewEntity.getPosX() / 1000 * 1000;
            cameraOffsetZ = (int)viewEntity.getPosZ() / 1000 * 1000;
        }
    }

    public static void setCameraShadow(MatrixStack matrixStack, ActiveRenderInfo activeRenderInfo, float partialTicks)
    {
        Entity entity = activeRenderInfo.getRenderViewEntity();
        Vector3d vector3d = activeRenderInfo.getProjectedView();
        double d0 = vector3d.x;
        double d1 = vector3d.y;
        double d2 = vector3d.z;
        updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        GL43.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        GL43.glMatrixMode(5889);
        GL43.glLoadIdentity();

        if (shadowMapIsOrtho)
        {
            GL43.glOrtho((double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)0.05F, 256.0D);
        }
        else
        {
            GlStateManager.multMatrix(Matrix4f.perspective((double)shadowMapFOV, (float)shadowMapWidth / (float)shadowMapHeight, 0.05F, 256.0F));
        }

        matrixStack.translate(0.0D, 0.0D, -100.0D);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        celestialAngle = mc.world.func_242415_f(partialTicks);
        sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
        float f = celestialAngle * -360.0F;
        float f1 = shadowAngleInterval > 0.0F ? f % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;

        if ((double)sunAngle <= 0.5D)
        {
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(f - f1));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(sunPathRotation));
            shadowAngle = sunAngle;
        }
        else
        {
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(f + 180.0F - f1));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(sunPathRotation));
            shadowAngle = sunAngle - 0.5F;
        }

        if (shadowMapIsOrtho)
        {
            float f2 = shadowIntervalSize;
            float f3 = f2 / 2.0F;
            matrixStack.translate((double)((float)d0 % f2 - f3), (double)((float)d1 % f2 - f3), (double)((float)d2 % f2 - f3));
        }

        float f9 = sunAngle * ((float)Math.PI * 2F);
        float f10 = (float)Math.cos((double)f9);
        float f4 = (float)Math.sin((double)f9);
        float f5 = sunPathRotation * ((float)Math.PI * 2F);
        float f6 = f10;
        float f7 = f4 * (float)Math.cos((double)f5);
        float f8 = f4 * (float)Math.sin((double)f5);

        if ((double)sunAngle > 0.5D)
        {
            f6 = -f10;
            f7 = -f7;
            f8 = -f8;
        }

        shadowLightPositionVector[0] = f6;
        shadowLightPositionVector[1] = f7;
        shadowLightPositionVector[2] = f8;
        shadowLightPositionVector[3] = 0.0F;
        updateShadowProjectionMatrix();
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        matrix4f.write((FloatBuffer)((Buffer)shadowModelView).position(0));
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)shadowModelViewInverse).position(0), (FloatBuffer)((Buffer)shadowModelView).position(0), faShadowModelViewInverse, faShadowModelView);
        ((Buffer)shadowModelView).position(0);
        ((Buffer)shadowModelViewInverse).position(0);
        setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
        setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
        setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
        setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
        setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
        setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
        setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
        setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
        checkGLError("setCamera");
    }

    public static void preCelestialRotate(MatrixStack matrixStackIn)
    {
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(sunPathRotation * 1.0F));
        checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate(MatrixStack matrixStackIn)
    {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        Matrix4f matrix4f1 = new Matrix4f(matrix4f);
        matrix4f1.transpose();
        matrix4f1.write(tempMat);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
        setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
        setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);

        if (customUniforms != null)
        {
            customUniforms.update();
        }

        checkGLError("postCelestialRotate");
    }

    public static void setUpPosition(MatrixStack matrixStackIn)
    {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        Matrix4f matrix4f1 = new Matrix4f(matrix4f);
        matrix4f1.transpose();
        matrix4f1.write(tempMat);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);

        if (customUniforms != null)
        {
            customUniforms.update();
        }
    }

    public static void drawComposite()
    {
        GL43.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawCompositeQuad();
        int i = activeProgram.getCountInstances();

        if (i > 1)
        {
            for (int j = 1; j < i; ++j)
            {
                uniform_instanceId.setValue(j);
                drawCompositeQuad();
            }

            uniform_instanceId.setValue(0);
        }
    }

    private static void drawCompositeQuad()
    {
        if (!canRenderQuads())
        {
            GL43.glBegin(5);
            GL43.glTexCoord2f(0.0F, 0.0F);
            GL43.glVertex3f(0.0F, 0.0F, 0.0F);
            GL43.glTexCoord2f(1.0F, 0.0F);
            GL43.glVertex3f(1.0F, 0.0F, 0.0F);
            GL43.glTexCoord2f(0.0F, 1.0F);
            GL43.glVertex3f(0.0F, 1.0F, 0.0F);
            GL43.glTexCoord2f(1.0F, 1.0F);
            GL43.glVertex3f(1.0F, 1.0F, 0.0F);
            GL43.glEnd();
        }
        else
        {
            GL43.glBegin(7);
            GL43.glTexCoord2f(0.0F, 0.0F);
            GL43.glVertex3f(0.0F, 0.0F, 0.0F);
            GL43.glTexCoord2f(1.0F, 0.0F);
            GL43.glVertex3f(1.0F, 0.0F, 0.0F);
            GL43.glTexCoord2f(1.0F, 1.0F);
            GL43.glVertex3f(1.0F, 1.0F, 0.0F);
            GL43.glTexCoord2f(0.0F, 1.0F);
            GL43.glVertex3f(0.0F, 1.0F, 0.0F);
            GL43.glEnd();
        }
    }

    public static void renderDeferred()
    {
        if (!isShadowPass)
        {
            boolean flag = checkBufferFlip(dfb, ProgramDeferredPre);

            if (hasDeferredPrograms)
            {
                checkGLError("pre-render Deferred");
                renderDeferredComposites();
                flag = true;
            }

            if (flag)
            {
                bindGbuffersTextures();
                dfb.setColorTextures(true);
                DrawBuffers drawbuffers = ProgramWater.getDrawBuffers() != null ? ProgramWater.getDrawBuffers() : dfb.getDrawBuffers();
                GlState.setDrawBuffers(drawbuffers);
                GlStateManager.activeTexture(33984);
                mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            }
        }
    }

    public static void renderCompositeFinal()
    {
        if (!isShadowPass)
        {
            checkBufferFlip(dfb, ProgramCompositePre);
            checkGLError("pre-render CompositeFinal");
            renderComposites();
        }
    }

    private static boolean checkBufferFlip(ShadersFramebuffer framebuffer, Program program)
    {
        boolean flag = false;
        Boolean[] aboolean = program.getBuffersFlip();

        for (int i = 0; i < usedColorBuffers; ++i)
        {
            if (Config.isTrue(aboolean[i]))
            {
                framebuffer.flipColorTexture(i);
                flag = true;
            }
        }

        return flag;
    }

    private static void renderComposites()
    {
        if (!isShadowPass)
        {
            renderComposites(ProgramsComposite, true, customTexturesComposite);
        }
    }

    private static void renderDeferredComposites()
    {
        if (!isShadowPass)
        {
            renderComposites(ProgramsDeferred, false, customTexturesDeferred);
        }
    }

    public static void renderPrepareComposites()
    {
        renderComposites(ProgramsPrepare, false, customTexturesPrepare);
    }

    private static void renderComposites(Program[] ps, boolean renderFinal, ICustomTexture[] customTextures)
    {
        renderComposites(dfb, ps, renderFinal, customTextures);
    }

    public static void renderShadowComposites()
    {
        renderComposites(sfb, ProgramsShadowcomp, false, customTexturesShadowcomp);
    }

    private static void renderComposites(ShadersFramebuffer framebuffer, Program[] ps, boolean renderFinal, ICustomTexture[] customTextures)
    {
        GL43.glPushMatrix();
        GL43.glLoadIdentity();
        GL43.glMatrixMode(5889);
        GL43.glPushMatrix();
        GL43.glLoadIdentity();
        GL43.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D);
        GL43.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        bindTextures(0, customTextures);
        framebuffer.bindColorImages(true);
        framebuffer.setColorTextures(false);
        framebuffer.setDepthTexture();
        framebuffer.setDrawBuffers();
        checkGLError("pre-composite");

        for (int i = 0; i < ps.length; ++i)
        {
            Program program = ps[i];
            dispatchComputes(framebuffer, program.getComputePrograms());

            if (program.getId() != 0)
            {
                useProgram(program);
                checkGLError(program.getName());

                if (program.hasCompositeMipmaps())
                {
                    framebuffer.genCompositeMipmap(program.getCompositeMipmapSetting());
                }

                preDrawComposite(framebuffer, program);
                drawComposite();
                postDrawComposite(framebuffer, program);
                framebuffer.flipColorTextures(program.getToggleColorTextures());
            }
        }

        checkGLError("composite");

        if (renderFinal)
        {
            renderFinal();
            isCompositeRendered = true;
        }

        GlStateManager.enableTexture();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GL43.glPopMatrix();
        GL43.glMatrixMode(5888);
        GL43.glPopMatrix();
        useProgram(ProgramNone);
    }

    private static void preDrawComposite(ShadersFramebuffer framebuffer, Program program)
    {
        int i = framebuffer.getWidth();
        int j = framebuffer.getHeight();

        if (program.getDrawSize() != null)
        {
            Dimension dimension = program.getDrawSize().getDimension(i, j);
            i = dimension.width;
            j = dimension.height;
            FixedFramebuffer fixedframebuffer = framebuffer.getFixedFramebuffer(i, j, program.getDrawBuffers(), false);
            fixedframebuffer.bindFramebuffer();
            GL43.glViewport(0, 0, i, j);
        }

        RenderScale renderscale = program.getRenderScale();

        if (renderscale != null)
        {
            int j1 = (int)((float)i * renderscale.getOffsetX());
            int k = (int)((float)j * renderscale.getOffsetY());
            int l = (int)((float)i * renderscale.getScale());
            int i1 = (int)((float)j * renderscale.getScale());
            GL43.glViewport(j1, k, l, i1);
        }
    }

    private static void postDrawComposite(ShadersFramebuffer framebuffer, Program program)
    {
        if (program.getDrawSize() != null)
        {
            framebuffer.bindFramebuffer();
            GL43.glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
        }

        RenderScale renderscale = activeProgram.getRenderScale();

        if (renderscale != null)
        {
            GL43.glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
        }
    }

    public static void dispatchComputes(ShadersFramebuffer framebuffer, ComputeProgram[] cps)
    {
        for (int i = 0; i < cps.length; ++i)
        {
            ComputeProgram computeprogram = cps[i];
            dispatchCompute(computeprogram);

            if (computeprogram.hasCompositeMipmaps())
            {
                framebuffer.genCompositeMipmap(computeprogram.getCompositeMipmapSetting());
            }
        }
    }

    public static void dispatchCompute(ComputeProgram cp)
    {
        if (dfb != null)
        {
            ARBShaderObjects.glUseProgramObjectARB(cp.getId());

            if (checkGLError("useComputeProgram") != 0)
            {
                cp.setId(0);
            }
            else
            {
                shaderUniforms.setProgram(cp.getId());

                if (customUniforms != null)
                {
                    customUniforms.setProgram(cp.getId());
                }

                setProgramUniforms(cp.getProgramStage());
                setImageUniforms();
                dfb.bindColorImages(true);
                Vector3i vector3i = cp.getWorkGroups();

                if (vector3i == null)
                {
                    Vector2f vector2f = cp.getWorkGroupsRender();

                    if (vector2f == null)
                    {
                        vector2f = new Vector2f(1.0F, 1.0F);
                    }

                    int i = (int)Math.ceil((double)((float)renderWidth * vector2f.x));
                    int j = (int)Math.ceil((double)((float)renderHeight * vector2f.y));
                    Vector3i vector3i1 = cp.getLocalSize();
                    int k = (int)Math.ceil(1.0D * (double)i / (double)vector3i1.getX());
                    int l = (int)Math.ceil(1.0D * (double)j / (double)vector3i1.getY());
                    vector3i = new Vector3i(k, l, 1);
                }

                GL43.glMemoryBarrier(40);
                GL43.glDispatchCompute(vector3i.getX(), vector3i.getY(), vector3i.getZ());
                GL43.glMemoryBarrier(40);
                checkGLError("compute");
            }
        }
    }

    private static void renderFinal()
    {
        dispatchComputes(dfb, ProgramFinal.getComputePrograms());
        isRenderingDfb = false;
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.framebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, FramebufferConstants.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().func_242996_f(), 0);
        GL43.glViewport(0, 0, mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight());
        GlStateManager.depthMask(true);
        GL43.glClearColor(clearColor.getX(), clearColor.getY(), clearColor.getZ(), 1.0F);
        GL43.glClear(16640);
        GL43.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        checkGLError("pre-final");
        useProgram(ProgramFinal);
        checkGLError("final");

        if (ProgramFinal.hasCompositeMipmaps())
        {
            dfb.genCompositeMipmap(ProgramFinal.getCompositeMipmapSetting());
        }

        drawComposite();
        checkGLError("renderCompositeFinal");
    }

    public static void endRender()
    {
        if (isShadowPass)
        {
            checkGLError("shadow endRender");
        }
        else
        {
            if (!isCompositeRendered)
            {
                renderCompositeFinal();
            }

            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            useProgram(ProgramNone);
            setRenderStage(RenderStage.NONE);
            RenderHelper.disableStandardItemLighting();
            checkGLError("endRender end");
        }
    }

    public static void beginSky()
    {
        isRenderingSky = true;
        fogEnabled = true;
        useProgram(ProgramSkyTextured);
        pushEntity(-2, 0);
        setRenderStage(RenderStage.SKY);
    }

    public static void setSkyColor(Vector3d v3color)
    {
        skyColorR = (float)v3color.x;
        skyColorG = (float)v3color.y;
        skyColorB = (float)v3color.z;
        setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon(MatrixStack matrixStackIn)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        float f = (float)(mc.gameSettings.renderDistanceChunks * 16);
        double d0 = (double)f * 0.9238D;
        double d1 = (double)f * 0.3826D;
        double d2 = -d1;
        double d3 = -d0;
        double d4 = 16.0D;
        double d5 = -cameraPositionY + currentWorld.getWorldInfo().getVoidFogHeight() + 12.0D - 16.0D;

        if (cameraPositionY < currentWorld.getWorldInfo().getVoidFogHeight())
        {
            d5 = -4.0D;
        }

        GlStateManager.pushMatrix();
        GlStateManager.multMatrix(matrixStackIn.getLast().getMatrix());
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(d2, d5, d3).endVertex();
        bufferbuilder.pos(d2, d4, d3).endVertex();
        bufferbuilder.pos(d3, d4, d2).endVertex();
        bufferbuilder.pos(d3, d5, d2).endVertex();
        bufferbuilder.pos(d3, d5, d2).endVertex();
        bufferbuilder.pos(d3, d4, d2).endVertex();
        bufferbuilder.pos(d3, d4, d1).endVertex();
        bufferbuilder.pos(d3, d5, d1).endVertex();
        bufferbuilder.pos(d3, d5, d1).endVertex();
        bufferbuilder.pos(d3, d4, d1).endVertex();
        bufferbuilder.pos(d2, d4, d0).endVertex();
        bufferbuilder.pos(d2, d5, d0).endVertex();
        bufferbuilder.pos(d2, d5, d0).endVertex();
        bufferbuilder.pos(d2, d4, d0).endVertex();
        bufferbuilder.pos(d1, d4, d0).endVertex();
        bufferbuilder.pos(d1, d5, d0).endVertex();
        bufferbuilder.pos(d1, d5, d0).endVertex();
        bufferbuilder.pos(d1, d4, d0).endVertex();
        bufferbuilder.pos(d0, d4, d1).endVertex();
        bufferbuilder.pos(d0, d5, d1).endVertex();
        bufferbuilder.pos(d0, d5, d1).endVertex();
        bufferbuilder.pos(d0, d4, d1).endVertex();
        bufferbuilder.pos(d0, d4, d2).endVertex();
        bufferbuilder.pos(d0, d5, d2).endVertex();
        bufferbuilder.pos(d0, d5, d2).endVertex();
        bufferbuilder.pos(d0, d4, d2).endVertex();
        bufferbuilder.pos(d1, d4, d3).endVertex();
        bufferbuilder.pos(d1, d5, d3).endVertex();
        bufferbuilder.pos(d1, d5, d3).endVertex();
        bufferbuilder.pos(d1, d4, d3).endVertex();
        bufferbuilder.pos(d2, d4, d3).endVertex();
        bufferbuilder.pos(d2, d5, d3).endVertex();
        bufferbuilder.pos(d3, d5, d3).endVertex();
        bufferbuilder.pos(d3, d5, d0).endVertex();
        bufferbuilder.pos(d0, d5, d0).endVertex();
        bufferbuilder.pos(d0, d5, d3).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();
    }

    public static void preSkyList(MatrixStack matrixStackIn)
    {
        setUpPosition(matrixStackIn);
        GL11.glColor3f(fogColorR, fogColorG, fogColorB);
        drawHorizon(matrixStackIn);
        GL11.glColor3f(skyColorR, skyColorG, skyColorB);
    }

    public static void endSky()
    {
        isRenderingSky = false;
        useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        popEntity();
        setRenderStage(RenderStage.NONE);
    }

    public static void beginUpdateChunks()
    {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(ProgramTerrain);
        }

        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks()
    {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(ProgramTerrain);
        }

        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs)
    {
        if (!shaderPackLoaded)
        {
            return true;
        }
        else
        {
            checkGLError("shouldRenderClouds");
            return isShadowPass ? configCloudShadow : gs.cloudOption != CloudOption.OFF;
        }
    }

    public static void beginClouds()
    {
        fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(ProgramClouds);
        setRenderStage(RenderStage.CLOUDS);
    }

    public static void endClouds()
    {
        disableFog();
        popEntity();
        useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        setRenderStage(RenderStage.NONE);
    }

    public static void beginEntities()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramEntities);
            setRenderStage(RenderStage.ENTITIES);
        }
    }

    public static void nextEntity(Entity entity)
    {
        if (isRenderingWorld)
        {
            if (entity.isGlowing())
            {
                useProgram(ProgramEntitiesGlowing);
            }
            else
            {
                useProgram(ProgramEntities);
            }

            setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity)
    {
        if (uniform_entityId.isDefined())
        {
            int i = EntityUtils.getEntityIdByClass(entity);
            int j = EntityAliases.getEntityAliasId(i);
            uniform_entityId.setValue(j);
        }
    }

    public static void beginSpiderEyes()
    {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId())
        {
            useProgram(ProgramSpiderEyes);
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, 0.0F);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes()
    {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId())
        {
            useProgram(ProgramEntities);
            GlStateManager.disableAlphaTest();
        }
    }

    public static void endEntities()
    {
        if (isRenderingWorld)
        {
            setEntityId((Entity)null);
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void beginEntitiesGlowing()
    {
        if (isRenderingWorld)
        {
            isEntitiesGlowing = true;
        }
    }

    public static void endEntitiesGlowing()
    {
        if (isRenderingWorld)
        {
            isEntitiesGlowing = false;
        }
    }

    public static void setEntityColor(float r, float g, float b, float a)
    {
        if (isRenderingWorld && !isShadowPass)
        {
            uniform_entityColor.setValue(r, g, b, a);
        }
    }

    public static void beginLivingDamage()
    {
        if (isRenderingWorld)
        {
            ShadersTex.bindTexture(defaultTexture);

            if (!isShadowPass)
            {
                GlState.setDrawBuffers(drawBuffersColorAtt[0]);
            }
        }
    }

    public static void endLivingDamage()
    {
        if (isRenderingWorld && !isShadowPass)
        {
            GlState.setDrawBuffers(ProgramEntities.getDrawBuffers());
        }
    }

    public static void beginBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("beginBlockEntities");
            useProgram(ProgramBlock);
            setRenderStage(RenderStage.BLOCK_ENTITIES);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity)
    {
        if (isRenderingWorld)
        {
            checkGLError("nextBlockEntity");
            useProgram(ProgramBlock);
            setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity)
    {
        if (uniform_blockEntityId.isDefined())
        {
            int i = getBlockEntityId(tileEntity);
            uniform_blockEntityId.setValue(i);
        }
    }

    private static int getBlockEntityId(TileEntity tileEntity)
    {
        if (tileEntity == null)
        {
            return -1;
        }
        else
        {
            BlockState blockstate = tileEntity.getBlockState();
            return BlockAliases.getAliasBlockId(blockstate);
        }
    }

    public static void endBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("endBlockEntities");
            setBlockEntityId((TileEntity)null);
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginLitParticles()
    {
        useProgram(ProgramTexturedLit);
    }

    public static void beginParticles()
    {
        useProgram(ProgramTextured);
        setRenderStage(RenderStage.PARTICLES);
    }

    public static void endParticles()
    {
        useProgram(ProgramTexturedLit);
        setRenderStage(RenderStage.NONE);
    }

    public static void readCenterDepth()
    {
        if (!isShadowPass && centerDepthSmoothEnabled)
        {
            ((Buffer)tempDirectFloatBuffer).clear();
            GL43.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f = (float)diffSystemTime * 0.01F;
            float f1 = (float)Math.exp(Math.log(0.5D) * (double)f / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * f1 + centerDepth * (1.0F - f1);
        }
    }

    public static void beginWeather()
    {
        if (!isShadowPass)
        {
            GlStateManager.enableDepthTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlphaTest();
            useProgram(ProgramWeather);
            setRenderStage(RenderStage.RAIN_SNOW);
        }
    }

    public static void endWeather()
    {
        GlStateManager.disableBlend();
        useProgram(ProgramTexturedLit);
        setRenderStage(RenderStage.NONE);
    }

    public static void preRenderHand()
    {
        if (!isShadowPass && usedDepthBuffers >= 3)
        {
            GlStateManager.activeTexture(33996);
            GL43.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            GlStateManager.activeTexture(33984);
        }
    }

    public static void preWater()
    {
        if (usedDepthBuffers >= 2)
        {
            GlStateManager.activeTexture(33995);
            checkGLError("pre copy depth");
            GL43.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            checkGLError("copy depth");
            GlStateManager.activeTexture(33984);
        }

        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater()
    {
        if (isRenderingWorld)
        {
            if (!isShadowPass)
            {
                renderDeferred();
                useProgram(ProgramWater);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            }
            else
            {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater()
    {
        if (isRenderingWorld)
        {
            if (isShadowPass)
            {
            }

            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void applyHandDepth(MatrixStack matrixStackIn)
    {
        if ((double)configHandDepthMul != 1.0D)
        {
            matrixStackIn.scale(1.0F, 1.0F, configHandDepthMul);
        }
    }

    public static void beginHand(MatrixStack matrixStackIn, boolean translucent)
    {
        GL43.glMatrixMode(5888);
        GL43.glPushMatrix();
        GL43.glMatrixMode(5889);
        GL43.glPushMatrix();
        GL43.glMatrixMode(5888);
        matrixStackIn.push();

        if (translucent)
        {
            useProgram(ProgramHandWater);
        }
        else
        {
            useProgram(ProgramHand);
        }

        checkGLError("beginHand");
        checkFramebufferStatus("beginHand");
    }

    public static void endHand(MatrixStack matrixStackIn)
    {
        checkGLError("pre endHand");
        checkFramebufferStatus("pre endHand");
        matrixStackIn.pop();
        GL43.glMatrixMode(5889);
        GL43.glPopMatrix();
        GL43.glMatrixMode(5888);
        GL43.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        checkGLError("endHand");
    }

    public static void beginFPOverlay()
    {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay()
    {
    }

    public static void glEnableWrapper(int cap)
    {
        GL43.glEnable(cap);

        if (cap == 3553)
        {
            enableTexture2D();
        }
        else if (cap == 2912)
        {
            enableFog();
        }
    }

    public static void glDisableWrapper(int cap)
    {
        GL43.glDisable(cap);

        if (cap == 3553)
        {
            disableTexture2D();
        }
        else if (cap == 2912)
        {
            disableFog();
        }
    }

    public static void sglEnableT2D(int cap)
    {
        GL43.glEnable(cap);
        enableTexture2D();
    }

    public static void sglDisableT2D(int cap)
    {
        GL43.glDisable(cap);
        disableTexture2D();
    }

    public static void sglEnableFog(int cap)
    {
        GL43.glEnable(cap);
        enableFog();
    }

    public static void sglDisableFog(int cap)
    {
        GL43.glDisable(cap);
        disableFog();
    }

    public static void enableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(ProgramSkyTextured);
        }
        else if (activeProgram == ProgramBasic)
        {
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void disableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(ProgramSkyBasic);
        }
        else if (activeProgram == ProgramTextured || activeProgram == ProgramTexturedLit)
        {
            useProgram(ProgramBasic);
        }
    }

    public static void pushProgram()
    {
        programStack.push(activeProgram);
    }

    public static void popProgram()
    {
        Program program = programStack.pop();
        useProgram(program);
    }

    public static void beginLeash()
    {
        pushProgram();
        useProgram(ProgramBasic);
    }

    public static void endLeash()
    {
        popProgram();
    }

    public static void enableFog()
    {
        fogEnabled = true;
        setProgramUniform1i(uniform_fogMode, fogMode);
        setProgramUniform1f(uniform_fogDensity, fogDensity);
    }

    public static void disableFog()
    {
        fogEnabled = false;
        setProgramUniform1i(uniform_fogMode, 0);
    }

    public static void setFogMode(GlStateManager.FogMode fogMode)
    {
        setFogMode(fogMode.param);
    }

    public static void setFogDensity(float value)
    {
        fogDensity = value;

        if (fogEnabled)
        {
            setProgramUniform1f(uniform_fogDensity, value);
        }
    }

    public static void sglFogi(int pname, int param)
    {
        GL11.glFogi(pname, param);

        if (pname == 2917)
        {
            fogMode = param;

            if (fogEnabled)
            {
                setProgramUniform1i(uniform_fogMode, fogMode);
            }
        }
    }

    public static void enableLightmap()
    {
        lightmapEnabled = true;

        if (activeProgram == ProgramTextured)
        {
            useProgram(ProgramTexturedLit);
        }
    }

    public static void disableLightmap()
    {
        lightmapEnabled = false;

        if (activeProgram == ProgramTexturedLit)
        {
            useProgram(ProgramTextured);
        }
    }

    public static int getEntityData()
    {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2()
    {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1)
    {
        entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2)
    {
        entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | data2 & 65535;
        return data2;
    }

    public static void pushEntity(int data0, int data1)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535 | data1 << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block)
    {
        ++entityDataIndex;
        int i = block.getRenderType(block.getDefaultState()).ordinal();
        entityData[entityDataIndex * 2] = Registry.BLOCK.getId(block) & 65535 | i << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void popEntity()
    {
        entityData[entityDataIndex * 2] = 0;
        entityData[entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection()
    {
        mc.getProfiler().endSection();
    }

    public static String getShaderPackName()
    {
        if (shaderPack == null)
        {
            return null;
        }
        else
        {
            return shaderPack instanceof ShaderPackNone ? null : shaderPack.getName();
        }
    }

    public static InputStream getShaderPackResourceStream(String path)
    {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel(boolean forward)
    {
        if (forward)
        {
            configAntialiasingLevel += 2;

            if (configAntialiasingLevel > 4)
            {
                configAntialiasingLevel = 0;
            }
        }
        else
        {
            configAntialiasingLevel -= 2;

            if (configAntialiasingLevel < 0)
            {
                configAntialiasingLevel = 4;
            }
        }

        configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled()
    {
        try
        {
            Class e = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable throwable)
        {
            return;
        }

        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }

    public static void resourcesReloaded()
    {
        loadShaderPackResources();
        reloadCustomTexturesLocation(customTexturesGbuffers);
        reloadCustomTexturesLocation(customTexturesComposite);
        reloadCustomTexturesLocation(customTexturesDeferred);
        reloadCustomTexturesLocation(customTexturesShadowcomp);
        reloadCustomTexturesLocation(customTexturesPrepare);

        if (shaderPackLoaded)
        {
            BlockAliases.resourcesReloaded();
            ItemAliases.resourcesReloaded();
            EntityAliases.resourcesReloaded();
        }
    }

    private static void loadShaderPackResources()
    {
        shaderPackResources = new HashMap<>();

        if (shaderPackLoaded)
        {
            List<String> list = new ArrayList<>();
            String s = "/shaders/lang/";
            String s1 = "en_us";
            String s2 = ".lang";
            list.add(s + s1 + s2);
            list.add(s + getLocaleUppercase(s1) + s2);

            if (!Config.getGameSettings().language.equals(s1))
            {
                String s3 = Config.getGameSettings().language;
                list.add(s + s3 + s2);
                list.add(s + getLocaleUppercase(s3) + s2);
            }

            try
            {
                for (String s4 : list)
                {
                    InputStream inputstream = shaderPack.getResourceAsStream(s4);

                    if (inputstream != null)
                    {
                        Properties properties = new PropertiesOrdered();
                        Lang.loadLocaleData(inputstream, properties);
                        inputstream.close();

                        for (String s5 : (Set<String>)(Set<?>)properties.keySet())
                        {
                            String s6 = properties.getProperty(s5);
                            shaderPackResources.put(s5, s6);
                        }
                    }
                }
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
    }

    private static String getLocaleUppercase(String name)
    {
        int i = name.indexOf(95);
        return i < 0 ? name : name.substring(0, i) + name.substring(i).toUpperCase(Locale.ROOT);
    }

    public static String translate(String key, String def)
    {
        String s = shaderPackResources.get(key);
        return s == null ? def : s;
    }

    public static boolean isProgramPath(String path)
    {
        if (path == null)
        {
            return false;
        }
        else if (path.length() <= 0)
        {
            return false;
        }
        else
        {
            int i = path.lastIndexOf("/");

            if (i >= 0)
            {
                path = path.substring(i + 1);
            }

            Program program = getProgram(path);
            return program != null;
        }
    }

    public static Program getProgram(String name)
    {
        return programs.getProgram(name);
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain)
    {
        itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
    }

    public static void setItemToRenderOff(ItemStack itemToRenderOff)
    {
        itemToRenderOffTranslucent = isTranslucentBlock(itemToRenderOff);
    }

    public static boolean isItemToRenderMainTranslucent()
    {
        return itemToRenderMainTranslucent;
    }

    public static boolean isItemToRenderOffTranslucent()
    {
        return itemToRenderOffTranslucent;
    }

    public static boolean isBothHandsRendered()
    {
        return isHandRenderedMain && isHandRenderedOff;
    }

    private static boolean isTranslucentBlock(ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        else
        {
            Item item = stack.getItem();

            if (item == null)
            {
                return false;
            }
            else if (!(item instanceof BlockItem))
            {
                return false;
            }
            else
            {
                BlockItem blockitem = (BlockItem)item;
                Block block = blockitem.getBlock();

                if (block == null)
                {
                    return false;
                }
                else
                {
                    RenderType rendertype = RenderTypeLookup.getChunkRenderType(block.getDefaultState());
                    return rendertype == RenderTypes.TRANSLUCENT;
                }
            }
        }
    }

    public static boolean isSkipRenderHand(Hand hand)
    {
        if (hand == Hand.MAIN_HAND && skipRenderHandMain)
        {
            return true;
        }
        else
        {
            return hand == Hand.OFF_HAND && skipRenderHandOff;
        }
    }

    public static boolean isRenderBothHands()
    {
        return !skipRenderHandMain && !skipRenderHandOff;
    }

    public static void setSkipRenderHands(boolean skipMain, boolean skipOff)
    {
        skipRenderHandMain = skipMain;
        skipRenderHandOff = skipOff;
    }

    public static void setHandsRendered(boolean handMain, boolean handOff)
    {
        isHandRenderedMain = handMain;
        isHandRenderedOff = handOff;
    }

    public static boolean isHandRenderedMain()
    {
        return isHandRenderedMain;
    }

    public static boolean isHandRenderedOff()
    {
        return isHandRenderedOff;
    }

    public static float getShadowRenderDistance()
    {
        return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void beginRenderFirstPersonHand(boolean translucent)
    {
        isRenderingFirstPersonHand = true;

        if (translucent)
        {
            setRenderStage(RenderStage.HAND_TRANSLUCENT);
        }
        else
        {
            setRenderStage(RenderStage.HAND_SOLID);
        }
    }

    public static void endRenderFirstPersonHand()
    {
        isRenderingFirstPersonHand = false;
        setRenderStage(RenderStage.NONE);
    }

    public static boolean isRenderingFirstPersonHand()
    {
        return isRenderingFirstPersonHand;
    }

    public static void beginBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramBeaconBeam);
        }
    }

    public static void endBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramBlock);
        }
    }

    public static ClientWorld getCurrentWorld()
    {
        return currentWorld;
    }

    public static BlockPos getWorldCameraPosition()
    {
        return new BlockPos(cameraPositionX + (double)cameraOffsetX, cameraPositionY, cameraPositionZ + (double)cameraOffsetZ);
    }

    public static boolean isCustomUniforms()
    {
        return customUniforms != null;
    }

    public static boolean canRenderQuads()
    {
        return hasGeometryShaders ? capabilities.GL_NV_geometry_shader4 : true;
    }

    public static boolean isOverlayDisabled()
    {
        return shaderPackLoaded;
    }

    public static boolean isRemapLightmap()
    {
        return shaderPackLoaded;
    }

    public static boolean isEffectsModelView()
    {
        return shaderPackLoaded;
    }

    public static void flushRenderBuffers()
    {
        RenderUtils.flushRenderBuffers();
    }

    public static void setRenderStage(RenderStage stage)
    {
        if (shaderPackLoaded)
        {
            renderStage = stage;
            uniform_renderStage.setValue(stage.ordinal());
        }
    }
}
