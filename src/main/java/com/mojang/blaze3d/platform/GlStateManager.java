package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.render.GlCullState;
import net.optifine.shaders.Shaders;
import net.optifine.util.LockCounter;
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBDrawBuffersBlend;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager
{
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), (p_lambda$static$0_0_) ->
    {
        LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_lambda$static$0_0_));
    });
    private static final GlStateManager.AlphaState ALPHA_TEST = new GlStateManager.AlphaState();
    private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
    private static final GlStateManager.BooleanState[] LIGHT_ENABLE = IntStream.range(0, 8).mapToObj((p_lambda$static$1_0_) ->
    {
        return new GlStateManager.BooleanState(16384 + p_lambda$static$1_0_);
    }).toArray((p_lambda$static$2_0_) ->
    {
        return new GlStateManager.BooleanState[p_lambda$static$2_0_];
    });
    private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
    private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
    private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
    private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
    private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
    private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
    private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
    private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
    private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
    private static final GlStateManager.ScissorState field_244591_n = new GlStateManager.ScissorState();
    private static final FloatBuffer FLOAT_4_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static int activeTexture;
    private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 32).mapToObj((p_lambda$static$3_0_) ->
    {
        return new GlStateManager.TextureState();
    }).toArray((p_lambda$static$4_0_) ->
    {
        return new GlStateManager.TextureState[p_lambda$static$4_0_];
    });
    private static int shadeModel = 7425;
    private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
    private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
    private static final GlStateManager.Color COLOR = new GlStateManager.Color();
    private static GlStateManager.FramebufferExtension fboMode;
    private static GlStateManager.SupportType supportType;
    private static LockCounter alphaLock = new LockCounter();
    private static GlAlphaState alphaLockState = new GlAlphaState();
    private static LockCounter blendLock = new LockCounter();
    private static GlBlendState blendLockState = new GlBlendState();
    private static LockCounter cullLock = new LockCounter();
    private static GlCullState cullLockState = new GlCullState();
    private static boolean clientStateLocked = false;
    private static int clientActiveTexture = 0;
    private static boolean creatingDisplayList = false;
    public static float lastBrightnessX = 0.0F;
    public static float lastBrightnessY = 0.0F;
    public static boolean openGL31;
    public static boolean vboRegions;
    public static int GL_COPY_READ_BUFFER;
    public static int GL_COPY_WRITE_BUFFER;
    public static int GL_ARRAY_BUFFER;
    public static int GL_STATIC_DRAW;
    private static boolean fogAllowed = true;
    public static final int GL_QUADS = 7;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_TEXTURE0 = 33984;
    public static final int GL_TEXTURE1 = 33985;
    public static final int GL_TEXTURE2 = 33986;
    private static int framebufferRead;
    private static int framebufferDraw;
    private static final int[] IMAGE_TEXTURES = new int[8];

    
    public static void pushLightingAttributes()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(8256);
    }

    
    public static void pushTextureAttributes()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(270336);
    }

    
    public static void popAttributes()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopAttrib();
    }

    
    public static void disableAlphaTest()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (alphaLock.isLocked())
        {
            alphaLockState.setDisabled();
        }
        else
        {
            ALPHA_TEST.test.disable();
        }
    }

    
    public static void enableAlphaTest()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (alphaLock.isLocked())
        {
            alphaLockState.setEnabled();
        }
        else
        {
            ALPHA_TEST.test.enable();
        }
    }

    
    public static void alphaFunc(int func, float ref)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (alphaLock.isLocked())
        {
            alphaLockState.setFuncRef(func, ref);
        }
        else
        {
            if (func != ALPHA_TEST.func || ref != ALPHA_TEST.ref)
            {
                ALPHA_TEST.func = func;
                ALPHA_TEST.ref = ref;
                GL11.glAlphaFunc(func, ref);
            }
        }
    }

    
    public static void enableLighting()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.enable();
    }

    
    public static void disableLighting()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.disable();
    }

    
    public static void enableLight(int light)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[light].enable();
    }

    
    public static void enableColorMaterial()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        COLOR_MATERIAL.colorMaterial.enable();
    }

    
    public static void disableColorMaterial()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        COLOR_MATERIAL.colorMaterial.disable();
    }

    
    public static void colorMaterial(int face, int mode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (face != COLOR_MATERIAL.face || mode != COLOR_MATERIAL.mode)
        {
            COLOR_MATERIAL.face = face;
            COLOR_MATERIAL.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    
    public static void light(int light, int pname, FloatBuffer params)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv(light, pname, params);
    }

    
    public static void lightModel(int pname, FloatBuffer params)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv(pname, params);
    }

    
    public static void normal3f(float nx, float ny, float nz)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void func_244593_j()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        field_244591_n.field_244595_a.disable();
    }

    public static void func_244594_k()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        field_244591_n.field_244595_a.enable();
    }

    public static void func_244592_a(int p_244592_0_, int p_244592_1_, int p_244592_2_, int p_244592_3_)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glScissor(p_244592_0_, p_244592_1_, p_244592_2_, p_244592_3_);
    }

    public static void disableDepthTest()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        DEPTH.test.disable();
    }

    public static void enableDepthTest()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        DEPTH.test.enable();
    }

    public static void depthFunc(int depthFunc)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (depthFunc != DEPTH.func)
        {
            DEPTH.func = depthFunc;
            GL11.glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (flagIn != DEPTH.mask)
        {
            DEPTH.mask = flagIn;
            GL11.glDepthMask(flagIn);
        }
    }

    public static void disableBlend()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (blendLock.isLocked())
        {
            blendLockState.setDisabled();
        }
        else
        {
            BLEND.blend.disable();
        }
    }

    public static void enableBlend()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (blendLock.isLocked())
        {
            blendLockState.setEnabled();
        }
        else
        {
            BLEND.blend.enable();
        }
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (blendLock.isLocked())
        {
            blendLockState.setFactors(srcFactor, dstFactor);
        }
        else
        {
            if (srcFactor != BLEND.srcFactorRgb || dstFactor != BLEND.dstFactorRgb || srcFactor != BLEND.srcFactorAlpha || dstFactor != BLEND.dstFactorAlpha)
            {
                BLEND.srcFactorRgb = srcFactor;
                BLEND.dstFactorRgb = dstFactor;
                BLEND.srcFactorAlpha = srcFactor;
                BLEND.dstFactorAlpha = dstFactor;

                if (Config.isShaders())
                {
                    Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactor, dstFactor);
                }

                GL11.glBlendFunc(srcFactor, dstFactor);
            }
        }
    }

    public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (blendLock.isLocked())
        {
            blendLockState.setFactors(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
        else
        {
            if (srcFactor != BLEND.srcFactorRgb || dstFactor != BLEND.dstFactorRgb || srcFactorAlpha != BLEND.srcFactorAlpha || dstFactorAlpha != BLEND.dstFactorAlpha)
            {
                BLEND.srcFactorRgb = srcFactor;
                BLEND.dstFactorRgb = dstFactor;
                BLEND.srcFactorAlpha = srcFactorAlpha;
                BLEND.dstFactorAlpha = dstFactorAlpha;

                if (Config.isShaders())
                {
                    Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
                }

                glBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
            }
        }
    }

    public static void blendColor(float red, float green, float blue, float alpha)
    {
        GL14.glBlendColor(red, green, blue, alpha);
    }

    public static void blendEquation(int blendEquation)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation(blendEquation);
    }

    public static String init(GLCapabilities glCapabilities)
    {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        Config.initDisplay();
        openGL31 = glCapabilities.OpenGL31;

        if (openGL31)
        {
            GL_COPY_READ_BUFFER = 36662;
            GL_COPY_WRITE_BUFFER = 36663;
        }
        else
        {
            GL_COPY_READ_BUFFER = 36662;
            GL_COPY_WRITE_BUFFER = 36663;
        }

        if (glCapabilities.OpenGL15)
        {
            GL_ARRAY_BUFFER = 34962;
            GL_STATIC_DRAW = 35044;
        }
        else
        {
            GL_ARRAY_BUFFER = 34962;
            GL_STATIC_DRAW = 35044;
        }

        boolean flag = openGL31 || glCapabilities.GL_ARB_copy_buffer;
        boolean flag1 = glCapabilities.OpenGL14;
        vboRegions = flag && flag1;

        if (!vboRegions)
        {
            List<String> list = new ArrayList<>();

            if (!flag)
            {
                list.add("OpenGL 1.3, ARB_copy_buffer");
            }

            if (!flag1)
            {
                list.add("OpenGL 1.4");
            }

            String s = "VboRegions not supported, missing: " + Config.listToString(list);
            Config.dbg(s);
            list.add(s);
        }

        if (glCapabilities.OpenGL30)
        {
            supportType = GlStateManager.SupportType.BASE;
        }
        else if (glCapabilities.GL_EXT_framebuffer_blit)
        {
            supportType = GlStateManager.SupportType.EXT;
        }
        else
        {
            supportType = GlStateManager.SupportType.NONE;
        }

        if (glCapabilities.OpenGL30)
        {
            fboMode = GlStateManager.FramebufferExtension.BASE;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "OpenGL 3.0";
        }
        else if (glCapabilities.GL_ARB_framebuffer_object)
        {
            fboMode = GlStateManager.FramebufferExtension.ARB;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "ARB_framebuffer_object extension";
        }
        else if (glCapabilities.GL_EXT_framebuffer_object)
        {
            fboMode = GlStateManager.FramebufferExtension.EXT;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "EXT_framebuffer_object extension";
        }
        else
        {
            throw new IllegalStateException("Could not initialize framebuffer support.");
        }
    }

    public static int getProgram(int program, int pname)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami(program, pname);
    }

    public static void attachShader(int program, int shaderIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader(program, shaderIn);
    }

    public static void deleteShader(int shaderIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader(shaderIn);
    }

    public static int createShader(int type)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader(type);
    }

    public static void shaderSource(int shaderIn, CharSequence source)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource(shaderIn, source);
    }

    public static void compileShader(int shaderIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader(shaderIn);
    }

    public static int getShader(int shaderIn, int pname)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi(shaderIn, pname);
    }

    public static void useProgram(int program)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram(program);
    }

    public static int createProgram()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void deleteProgram(int program)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram(program);
    }

    public static void linkProgram(int program)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram(program);
    }

    public static int getUniformLocation(int program, CharSequence name)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation(program, name);
    }

    public static void uniform1i(int location, IntBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv(location, value);
    }

    public static void uniform1i(int location, int value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i(location, value);
    }

    public static void uniform1f(int location, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv(location, value);
    }

    public static void uniform2i(int location, IntBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv(location, value);
    }

    public static void uniform2f(int location, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv(location, value);
    }

    public static void uniform3i(int location, IntBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv(location, value);
    }

    public static void uniform3f(int location, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv(location, value);
    }

    public static void uniform4i(int location, IntBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv(location, value);
    }

    public static void uniform4f(int location, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv(location, value);
    }

    public static void uniformMatrix2f(int location, boolean transpose, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv(location, transpose, value);
    }

    public static void uniformMatrix3f(int location, boolean transpose, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv(location, transpose, value);
    }

    public static void uniformMatrix4f(int location, boolean transpose, FloatBuffer value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    public static int getAttribLocation(int program, CharSequence name)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation(program, name);
    }

    public static int genBuffers()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void bindBuffer(int target, int buffer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer(target, buffer);
    }

    public static void bufferData(int target, ByteBuffer data, int usage)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData(target, data, usage);
    }

    public static void deleteBuffers(int buffer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers(buffer);
    }

    public static void copySubImage(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D(target, level, xOffset, yOffset, x, y, width, height);
    }

    public static void bindFramebuffer(int target, int framebufferIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (target == 36160)
        {
            if (framebufferRead == framebufferIn && framebufferDraw == framebufferIn)
            {
                return;
            }

            framebufferRead = framebufferIn;
            framebufferDraw = framebufferIn;
        }
        else if (target == 36008)
        {
            if (framebufferRead == framebufferIn)
            {
                return;
            }

            framebufferRead = framebufferIn;
        }

        if (target == 36009)
        {
            if (framebufferDraw == framebufferIn)
            {
                return;
            }

            framebufferDraw = framebufferIn;
        }

        switch (fboMode)
        {
            case BASE:
                GL30.glBindFramebuffer(target, framebufferIn);
                break;

            case ARB:
                ARBFramebufferObject.glBindFramebuffer(target, framebufferIn);
                break;

            case EXT:
                EXTFramebufferObject.glBindFramebufferEXT(target, framebufferIn);
        }
    }

    public static int getFrameBufferAttachmentParam()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (fboMode)
        {
            case BASE:
                if (GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890)
                {
                    return GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
                }

                break;

            case ARB:
                if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890)
                {
                    return ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
                }

                break;

            case EXT:
                if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36048) == 5890)
                {
                    return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36049);
                }
        }

        return 0;
    }

    public static void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (supportType)
        {
            case BASE:
                GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;

            case EXT:
                EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);

            case NONE:
        }
    }

    public static void deleteFramebuffers(int frameBuffer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (fboMode)
        {
            case BASE:
                GL30.glDeleteFramebuffers(frameBuffer);
                break;

            case ARB:
                ARBFramebufferObject.glDeleteFramebuffers(frameBuffer);
                break;

            case EXT:
                EXTFramebufferObject.glDeleteFramebuffersEXT(frameBuffer);
        }
    }

    public static int genFramebuffers()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (fboMode)
        {
            case BASE:
                return GL30.glGenFramebuffers();

            case ARB:
                return ARBFramebufferObject.glGenFramebuffers();

            case EXT:
                return EXTFramebufferObject.glGenFramebuffersEXT();

            default:
                return -1;
        }
    }

    public static int checkFramebufferStatus(int target)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (fboMode)
        {
            case BASE:
                return GL30.glCheckFramebufferStatus(target);

            case ARB:
                return ARBFramebufferObject.glCheckFramebufferStatus(target);

            case EXT:
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);

            default:
                return -1;
        }
    }

    public static void framebufferTexture2D(int target, int attachment, int texTarget, int texture, int level)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        switch (fboMode)
        {
            case BASE:
                GL30.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;

            case ARB:
                ARBFramebufferObject.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;

            case EXT:
                EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, texTarget, texture, level);
        }
    }

    
    public static int getActiveTextureId()
    {
        return TEXTURES[activeTexture].textureName;
    }

    public static void glActiveTexture(int textureIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture(textureIn);
    }

    
    public static void clientActiveTexture(int texture)
    {
        if (texture != clientActiveTexture)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL13.glClientActiveTexture(texture);
            clientActiveTexture = texture;
        }
    }

    
    public static void multiTexCoord2f(int texture, float s, float t)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f(texture, s, t);

        if (texture == 33986)
        {
            lastBrightnessX = s;
            lastBrightnessY = t;
        }
    }

    public static void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
    }

    public static String getShaderInfoLog(int shader, int maxLength)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    public static String getProgramInfoLog(int program, int maxLength)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    public static void setupOutline()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        texEnv(8960, 8704, 34160);
        color(7681, 34168);
    }

    public static void teardownOutline()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        texEnv(8960, 8704, 8448);
        color(8448, 5890, 34168, 34166);
    }

    public static void setupOverlayColor(int texture, int bitSpace)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        activeTexture(33985);
        enableTexture();
        matrixMode(5890);
        loadIdentity();
        float f = 1.0F / (float)(bitSpace - 1);
        scalef(f, f, f);
        matrixMode(5888);
        bindTexture(texture);
        texParameter(3553, 10241, 9728);
        texParameter(3553, 10240, 9728);
        texParameter(3553, 10242, 10496);
        texParameter(3553, 10243, 10496);
        texEnv(8960, 8704, 34160);
        color(34165, 34168, 5890, 5890);
        alpha(7681, 34168);
        activeTexture(33984);
    }

    public static void teardownOverlayColor()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        activeTexture(33985);
        disableTexture();
        activeTexture(33984);
    }

    private static void color(int color1, int color2)
    {
        texEnv(8960, 34161, color1);
        texEnv(8960, 34176, color2);
        texEnv(8960, 34192, 768);
    }

    private static void color(int red, int green, int blue, int alpha)
    {
        texEnv(8960, 34161, red);
        texEnv(8960, 34176, green);
        texEnv(8960, 34192, 768);
        texEnv(8960, 34177, blue);
        texEnv(8960, 34193, 768);
        texEnv(8960, 34178, alpha);
        texEnv(8960, 34194, 770);
    }

    private static void alpha(int alpha1, int alpha2)
    {
        texEnv(8960, 34162, alpha1);
        texEnv(8960, 34184, alpha2);
        texEnv(8960, 34200, 770);
    }

    public static void setupLighting(Vector3f lightingVector1, Vector3f lightingVector2, Matrix4f matrix)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        pushMatrix();
        loadIdentity();
        enableLight(0);
        enableLight(1);
        Vector4f vector4f = new Vector4f(lightingVector1);
        vector4f.transform(matrix);
        light(16384, 4611, getBuffer(vector4f.getX(), vector4f.getY(), vector4f.getZ(), 0.0F));
        float f = 0.6F;
        light(16384, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        light(16384, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        light(16384, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        Vector4f vector4f1 = new Vector4f(lightingVector2);
        vector4f1.transform(matrix);
        light(16385, 4611, getBuffer(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), 0.0F));
        light(16385, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        light(16385, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        light(16385, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        shadeModel(7424);
        float f1 = 0.4F;
        lightModel(2899, getBuffer(0.4F, 0.4F, 0.4F, 1.0F));
        popMatrix();
    }

    public static void setupScaledLighting(Vector3f lighting1, Vector3f lighting2)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.mul(Matrix4f.makeScale(1.0F, -1.0F, 1.0F));
        matrix4f.mul(Vector3f.YP.rotationDegrees(-22.5F));
        matrix4f.mul(Vector3f.XP.rotationDegrees(135.0F));
        setupLighting(lighting1, lighting2, matrix4f);
    }

    public static void setupGui3DMatrix(Vector3f lightingVector1, Vector3f lightingVector2)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.mul(Vector3f.YP.rotationDegrees(62.0F));
        matrix4f.mul(Vector3f.XP.rotationDegrees(185.5F));
        matrix4f.mul(Matrix4f.makeScale(1.0F, -1.0F, 1.0F));
        matrix4f.mul(Vector3f.YP.rotationDegrees(-22.5F));
        matrix4f.mul(Vector3f.XP.rotationDegrees(135.0F));
        setupLighting(lightingVector1, lightingVector2, matrix4f);
    }

    private static FloatBuffer getBuffer(float float1, float float2, float float3, float float4)
    {
        ((Buffer)FLOAT_4_BUFFER).clear();
        FLOAT_4_BUFFER.put(float1).put(float2).put(float3).put(float4);
        ((Buffer)FLOAT_4_BUFFER).flip();
        return FLOAT_4_BUFFER;
    }

    public static void setupEndPortalTexGen()
    {
        texGenMode(GlStateManager.TexGen.S, 9216);
        texGenMode(GlStateManager.TexGen.T, 9216);
        texGenMode(GlStateManager.TexGen.R, 9216);
        texGenParam(GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
        texGenParam(GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
        texGenParam(GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
        enableTexGen(GlStateManager.TexGen.S);
        enableTexGen(GlStateManager.TexGen.T);
        enableTexGen(GlStateManager.TexGen.R);
    }

    public static void clearTexGen()
    {
        disableTexGen(GlStateManager.TexGen.S);
        disableTexGen(GlStateManager.TexGen.T);
        disableTexGen(GlStateManager.TexGen.R);
    }

    public static void mulTextureByProjModelView()
    {
        getMatrix(2983, MATRIX_BUFFER);
        multMatrix(MATRIX_BUFFER);
        getMatrix(2982, MATRIX_BUFFER);
        multMatrix(MATRIX_BUFFER);
    }

    
    public static void enableFog()
    {
        if (fogAllowed)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            FOG.fog.enable();
        }
    }

    
    public static void disableFog()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        FOG.fog.disable();
    }

    
    public static void fogMode(int fogMode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (fogMode != FOG.mode)
        {
            FOG.mode = fogMode;
            fogi(2917, fogMode);

            if (Config.isShaders())
            {
                Shaders.setFogMode(fogMode);
            }
        }
    }

    
    public static void fogDensity(float param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (param < 0.0F)
        {
            param = 0.0F;
        }

        if (param != FOG.density)
        {
            FOG.density = param;
            GL11.glFogf(GL11.GL_FOG_DENSITY, param);

            if (Config.isShaders())
            {
                Shaders.setFogDensity(param);
            }
        }
    }

    
    public static void fogStart(float param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (param != FOG.start)
        {
            FOG.start = param;
            GL11.glFogf(GL11.GL_FOG_START, param);
        }
    }

    
    public static void fogEnd(float param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (param != FOG.end)
        {
            FOG.end = param;
            GL11.glFogf(GL11.GL_FOG_END, param);
        }
    }

    
    public static void fog(int pname, float[] param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv(pname, param);
    }

    
    public static void fogi(int pname, int param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi(pname, param);
    }

    public static void enableCull()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (cullLock.isLocked())
        {
            cullLockState.setEnabled();
        }
        else
        {
            CULL.cullFace.enable();
        }
    }

    public static void disableCull()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (cullLock.isLocked())
        {
            cullLockState.setDisabled();
        }
        else
        {
            CULL.cullFace.disable();
        }
    }

    public static void polygonMode(int face, int mode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode(face, mode);
    }

    public static void enablePolygonOffset()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        POLY_OFFSET.polyOffset.enable();
    }

    public static void disablePolygonOffset()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        POLY_OFFSET.polyOffset.disable();
    }

    public static void enableLineOffset()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        POLY_OFFSET.lineOffset.enable();
    }

    public static void disableLineOffset()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        POLY_OFFSET.lineOffset.disable();
    }

    public static void polygonOffset(float factor, float units)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (factor != POLY_OFFSET.factor || units != POLY_OFFSET.units)
        {
            POLY_OFFSET.factor = factor;
            POLY_OFFSET.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogicOp()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        COLOR_LOGIC.colorLogicOp.enable();
    }

    public static void disableColorLogicOp()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        COLOR_LOGIC.colorLogicOp.disable();
    }

    public static void logicOp(int logicOperation)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (logicOperation != COLOR_LOGIC.logicOpcode)
        {
            COLOR_LOGIC.logicOpcode = logicOperation;
            GL11.glLogicOp(logicOperation);
        }
    }

    
    public static void enableTexGen(GlStateManager.TexGen texGen)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        getTexGen(texGen).textureGen.enable();
    }

    
    public static void disableTexGen(GlStateManager.TexGen texGen)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        getTexGen(texGen).textureGen.disable();
    }

    
    public static void texGenMode(GlStateManager.TexGen texGen, int mode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.TexGenCoord glstatemanager$texgencoord = getTexGen(texGen);

        if (mode != glstatemanager$texgencoord.mode)
        {
            glstatemanager$texgencoord.mode = mode;
            GL11.glTexGeni(glstatemanager$texgencoord.coord, GL11.GL_TEXTURE_GEN_MODE, mode);
        }
    }

    
    public static void texGenParam(GlStateManager.TexGen texGen, int pname, FloatBuffer params)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv(getTexGen(texGen).coord, pname, params);
    }

    
    private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen texGen)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        switch (texGen)
        {
            case S:
                return TEX_GEN.s;

            case T:
                return TEX_GEN.t;

            case R:
                return TEX_GEN.r;

            case Q:
                return TEX_GEN.q;

            default:
                return TEX_GEN.s;
        }
    }

    public static void activeTexture(int textureIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (activeTexture != textureIn - 33984)
        {
            activeTexture = textureIn - 33984;
            glActiveTexture(textureIn);
        }
    }

    public static void enableTexture()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        TEXTURES[activeTexture].texture2DState.enable();
    }

    public static void disableTexture()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TEXTURES[activeTexture].texture2DState.disable();
    }

    
    public static void texEnv(int target, int parameterName, int parameters)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi(target, parameterName, parameters);
    }

    public static void texParameter(int target, int parameterName, float parameter)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf(target, parameterName, parameter);
    }

    public static void texParameter(int target, int parameterName, int parameter)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri(target, parameterName, parameter);
    }

    public static int getTexLevelParameter(int target, int level, int parameterName)
    {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri(target, level, parameterName);
    }

    public static int genTexture()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void genTextures(int[] textures)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glGenTextures(textures);
    }

    public static void deleteTexture(int textureIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (textureIn != 0)
        {
            for (int i = 0; i < IMAGE_TEXTURES.length; ++i)
            {
                if (IMAGE_TEXTURES[i] == textureIn)
                {
                    IMAGE_TEXTURES[i] = 0;
                }
            }

            GL11.glDeleteTextures(textureIn);

            for (GlStateManager.TextureState glstatemanager$texturestate : TEXTURES)
            {
                if (glstatemanager$texturestate.textureName == textureIn)
                {
                    glstatemanager$texturestate.textureName = 0;
                }
            }
        }
    }

    public static void deleteTextures(int[] textures)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        for (GlStateManager.TextureState glstatemanager$texturestate : TEXTURES)
        {
            for (int i : textures)
            {
                if (glstatemanager$texturestate.textureName == i)
                {
                    glstatemanager$texturestate.textureName = -1;
                }
            }
        }

        GL11.glDeleteTextures(textures);
    }

    public static void bindTexture(int textureIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (textureIn != TEXTURES[activeTexture].textureName)
        {
            TEXTURES[activeTexture].textureName = textureIn;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIn);

            if (SmartAnimations.isActive())
            {
                SmartAnimations.textureRendered(textureIn);
            }
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long pixels)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    public static void getTexImage(int tex, int level, int format, int type, long pixels)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage(tex, level, format, type, pixels);
    }

    
    public static void shadeModel(int mode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (mode != shadeModel)
        {
            shadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    
    public static void enableRescaleNormal()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.enable();
    }

    
    public static void disableRescaleNormal()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.disable();
    }

    public static void viewport(int x, int y, int width, int height)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.Viewport.INSTANCE.x = x;
        GlStateManager.Viewport.INSTANCE.y = y;
        GlStateManager.Viewport.INSTANCE.w = width;
        GlStateManager.Viewport.INSTANCE.h = height;
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (red != COLOR_MASK.red || green != COLOR_MASK.green || blue != COLOR_MASK.blue || alpha != COLOR_MASK.alpha)
        {
            COLOR_MASK.red = red;
            COLOR_MASK.green = green;
            COLOR_MASK.blue = blue;
            COLOR_MASK.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void stencilFunc(int func, int ref, int mask)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (func != STENCIL.func.func || func != STENCIL.func.ref || func != STENCIL.func.mask)
        {
            STENCIL.func.func = func;
            STENCIL.func.ref = ref;
            STENCIL.func.mask = mask;
            GL11.glStencilFunc(func, ref, mask);
        }
    }

    public static void stencilMask(int mask)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (mask != STENCIL.mask)
        {
            STENCIL.mask = mask;
            GL11.glStencilMask(mask);
        }
    }

    public static void stencilOp(int sfail, int dpfail, int dppass)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (sfail != STENCIL.sfail || dpfail != STENCIL.dpfail || dppass != STENCIL.dppass)
        {
            STENCIL.sfail = sfail;
            STENCIL.dpfail = dpfail;
            STENCIL.dppass = dppass;
            GL11.glStencilOp(sfail, dpfail, dppass);
        }
    }

    public static void clearDepth(double depth)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearColor(red, green, blue, alpha);
    }

    public static void clearStencil(int index)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glClearStencil(index);
    }

    public static void clear(int mask, boolean checkError)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear(mask);

        if (checkError)
        {
            getError();
        }
    }

    
    public static void matrixMode(int mode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode(mode);
    }

    
    public static void loadIdentity()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glLoadIdentity();
    }

    
    public static void pushMatrix()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
    }

    
    public static void popMatrix()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
    }

    
    public static void getMatrix(int pname, FloatBuffer params)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv(pname, params);
    }

    
    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    
    public static void rotatef(float angle, float x, float y, float z)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef(angle, x, y, z);
    }

    
    public static void scalef(float x, float y, float z)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef(x, y, z);
    }

    
    public static void scaled(double x, double y, double z)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled(x, y, z);
    }

    
    public static void translatef(float x, float y, float z)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef(x, y, z);
    }

    
    public static void translated(double x, double y, double z)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated(x, y, z);
    }

    
    public static void multMatrix(FloatBuffer matrix)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf(matrix);
    }

    
    public static void multMatrix(Matrix4f matrix)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrix.write(MATRIX_BUFFER);
        ((Buffer)MATRIX_BUFFER).rewind();
        multMatrix(MATRIX_BUFFER);
    }

    
    public static void color4f(float red, float green, float blue, float alpha)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (red != COLOR.red || green != COLOR.green || blue != COLOR.blue || alpha != COLOR.alpha)
        {
            COLOR.red = red;
            COLOR.green = green;
            COLOR.blue = blue;
            COLOR.alpha = alpha;
            GL11.glColor4f(red, green, blue, alpha);
        }
    }

    
    public static void clearCurrentColor()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        COLOR.red = -1.0F;
        COLOR.green = -1.0F;
        COLOR.blue = -1.0F;
        COLOR.alpha = -1.0F;
    }

    
    public static void normalPointer(int type, int stride, long pointer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer(type, stride, pointer);
    }

    
    public static void texCoordPointer(int size, int type, int stride, long pointer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    
    public static void vertexPointer(int size, int type, int stride, long pointer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer(size, type, stride, pointer);
    }

    
    public static void colorPointer(int size, int type, int stride, long pointer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    
    public static void enableClientState(int cap)
    {
        if (!clientStateLocked)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL11.glEnableClientState(cap);
        }
    }

    
    public static void disableClientState(int cap)
    {
        if (!clientStateLocked)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL11.glDisableClientState(cap);
        }
    }

    public static void enableVertexAttribArray(int index)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void glEnableVertexAttribArray(int index)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void drawArrays(int mode, int first, int count)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawArrays(mode, first, count);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL11.glDrawArrays(mode, first, count);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void lineWidth(float width)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth(width);
    }

    public static void pixelStore(int pname, int param)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei(pname, param);
    }

    public static void pixelTransfer(int param, float value)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf(param, value);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }

    public static int getError()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String getString(int name)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString(name);
    }

    public static int getInteger(int pname)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger(pname);
    }

    public static boolean isFabulous()
    {
        return supportType != GlStateManager.SupportType.NONE;
    }

    public static int getActiveTextureUnit()
    {
        return 33984 + activeTexture;
    }

    public static void bindCurrentTexture()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXTURES[activeTexture].textureName);
    }

    public static int getBoundTexture()
    {
        return TEXTURES[activeTexture].textureName;
    }

    public static void checkBoundTexture()
    {
        if (Config.isMinecraftThread())
        {
            int i = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            int j = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            int k = getActiveTextureUnit();
            int l = getBoundTexture();

            if (l > 0)
            {
                if (i != k || j != l)
                {
                    Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
                }
            }
        }
    }

    public static void genTextures(IntBuffer p_genTextures_0_)
    {
        GL11.glGenTextures(p_genTextures_0_);
    }

    public static void deleteTextures(IntBuffer p_deleteTextures_0_)
    {
        ((Buffer)p_deleteTextures_0_).rewind();

        while (p_deleteTextures_0_.position() < p_deleteTextures_0_.limit())
        {
            int i = p_deleteTextures_0_.get();
            deleteTexture(i);
        }

        ((Buffer)p_deleteTextures_0_).rewind();
    }

    public static boolean isFogEnabled()
    {
        return FOG.fog.currentState;
    }

    public static void setFogEnabled(boolean p_setFogEnabled_0_)
    {
        FOG.fog.setEnabled(p_setFogEnabled_0_);
    }

    public static void lockAlpha(GlAlphaState p_lockAlpha_0_)
    {
        if (!alphaLock.isLocked())
        {
            getAlphaState(alphaLockState);
            setAlphaState(p_lockAlpha_0_);
            alphaLock.lock();
        }
    }

    public static void unlockAlpha()
    {
        if (alphaLock.unlock())
        {
            setAlphaState(alphaLockState);
        }
    }

    public static void getAlphaState(GlAlphaState p_getAlphaState_0_)
    {
        if (alphaLock.isLocked())
        {
            p_getAlphaState_0_.setState(alphaLockState);
        }
        else
        {
            p_getAlphaState_0_.setState(ALPHA_TEST.test.currentState, ALPHA_TEST.func, ALPHA_TEST.ref);
        }
    }

    public static void setAlphaState(GlAlphaState p_setAlphaState_0_)
    {
        if (alphaLock.isLocked())
        {
            alphaLockState.setState(p_setAlphaState_0_);
        }
        else
        {
            ALPHA_TEST.test.setEnabled(p_setAlphaState_0_.isEnabled());
            alphaFunc(p_setAlphaState_0_.getFunc(), p_setAlphaState_0_.getRef());
        }
    }

    public static void lockBlend(GlBlendState p_lockBlend_0_)
    {
        if (!blendLock.isLocked())
        {
            getBlendState(blendLockState);
            setBlendState(p_lockBlend_0_);
            blendLock.lock();
        }
    }

    public static void unlockBlend()
    {
        if (blendLock.unlock())
        {
            setBlendState(blendLockState);
        }
    }

    public static void getBlendState(GlBlendState p_getBlendState_0_)
    {
        if (blendLock.isLocked())
        {
            p_getBlendState_0_.setState(blendLockState);
        }
        else
        {
            p_getBlendState_0_.setState(BLEND.blend.currentState, BLEND.srcFactorRgb, BLEND.dstFactorRgb, BLEND.srcFactorAlpha, BLEND.dstFactorAlpha);
        }
    }

    public static void setBlendState(GlBlendState p_setBlendState_0_)
    {
        if (blendLock.isLocked())
        {
            blendLockState.setState(p_setBlendState_0_);
        }
        else
        {
            BLEND.blend.setEnabled(p_setBlendState_0_.isEnabled());

            if (!p_setBlendState_0_.isSeparate())
            {
                blendFunc(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor());
            }
            else
            {
                blendFuncSeparate(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor(), p_setBlendState_0_.getSrcFactorAlpha(), p_setBlendState_0_.getDstFactorAlpha());
            }
        }
    }

    public static void lockCull(GlCullState p_lockCull_0_)
    {
        if (!cullLock.isLocked())
        {
            getCullState(cullLockState);
            setCullState(p_lockCull_0_);
            cullLock.lock();
        }
    }

    public static void unlockCull()
    {
        if (cullLock.unlock())
        {
            setCullState(cullLockState);
        }
    }

    public static void getCullState(GlCullState p_getCullState_0_)
    {
        if (cullLock.isLocked())
        {
            p_getCullState_0_.setState(cullLockState);
        }
        else
        {
            p_getCullState_0_.setState(CULL.cullFace.currentState, CULL.mode);
        }
    }

    public static void setCullState(GlCullState p_setCullState_0_)
    {
        if (cullLock.isLocked())
        {
            cullLockState.setState(p_setCullState_0_);
        }
        else
        {
            CULL.cullFace.setEnabled(p_setCullState_0_.isEnabled());
            CULL.mode = p_setCullState_0_.getMode();
        }
    }

    public static void glMultiDrawArrays(int p_glMultiDrawArrays_0_, IntBuffer p_glMultiDrawArrays_1_, IntBuffer p_glMultiDrawArrays_2_)
    {
        GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void clear(int p_clear_0_)
    {
        clear(p_clear_0_, false);
    }

    public static void callLists(IntBuffer p_callLists_0_)
    {
        GL11.glCallLists(p_callLists_0_);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL11.glCallLists(p_callLists_0_);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void bufferData(int p_bufferData_0_, long p_bufferData_1_, int p_bufferData_3_)
    {
        GL15.glBufferData(p_bufferData_0_, p_bufferData_1_, p_bufferData_3_);
    }

    public static void bufferSubData(int p_bufferSubData_0_, long p_bufferSubData_1_, ByteBuffer p_bufferSubData_3_)
    {
        GL15.glBufferSubData(p_bufferSubData_0_, p_bufferSubData_1_, p_bufferSubData_3_);
    }

    public static void copyBufferSubData(int p_copyBufferSubData_0_, int p_copyBufferSubData_1_, long p_copyBufferSubData_2_, long p_copyBufferSubData_4_, long p_copyBufferSubData_6_)
    {
        if (openGL31)
        {
            GL31.glCopyBufferSubData(p_copyBufferSubData_0_, p_copyBufferSubData_1_, p_copyBufferSubData_2_, p_copyBufferSubData_4_, p_copyBufferSubData_6_);
        }
        else
        {
            ARBCopyBuffer.glCopyBufferSubData(p_copyBufferSubData_0_, p_copyBufferSubData_1_, p_copyBufferSubData_2_, p_copyBufferSubData_4_, p_copyBufferSubData_6_);
        }
    }

    public static boolean isFogAllowed()
    {
        return fogAllowed;
    }

    public static void setFogAllowed(boolean p_setFogAllowed_0_)
    {
        fogAllowed = p_setFogAllowed_0_;
    }

    public static void lockClientState()
    {
        clientStateLocked = true;
    }

    public static void unlockClientState()
    {
        clientStateLocked = false;
    }

    public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, long p_readPixels_6_)
    {
        GL11.glReadPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
    }

    public static int getFramebufferRead()
    {
        return framebufferRead;
    }

    public static int getFramebufferDraw()
    {
        return framebufferDraw;
    }

    public static void applyCurrentBlend()
    {
        if (BLEND.blend.currentState)
        {
            GL11.glEnable(GL11.GL_BLEND);
        }
        else
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL14.glBlendFuncSeparate(BLEND.srcFactorRgb, BLEND.dstFactorRgb, BLEND.srcFactorAlpha, BLEND.dstFactorAlpha);
    }

    public static void setBlendsIndexed(GlBlendState[] p_setBlendsIndexed_0_)
    {
        if (p_setBlendsIndexed_0_ != null)
        {
            for (int i = 0; i < p_setBlendsIndexed_0_.length; ++i)
            {
                GlBlendState glblendstate = p_setBlendsIndexed_0_[i];

                if (glblendstate != null)
                {
                    if (glblendstate.isEnabled())
                    {
                        GL30.glEnablei(3042, i);
                    }
                    else
                    {
                        GL30.glDisablei(3042, i);
                    }

                    ARBDrawBuffersBlend.glBlendFuncSeparateiARB(i, glblendstate.getSrcFactor(), glblendstate.getDstFactor(), glblendstate.getSrcFactorAlpha(), glblendstate.getDstFactorAlpha());
                }
            }
        }
    }

    public static void bindImageTexture(int p_bindImageTexture_0_, int p_bindImageTexture_1_, int p_bindImageTexture_2_, boolean p_bindImageTexture_3_, int p_bindImageTexture_4_, int p_bindImageTexture_5_, int p_bindImageTexture_6_)
    {
        if (p_bindImageTexture_0_ >= 0 && p_bindImageTexture_0_ < IMAGE_TEXTURES.length)
        {
            if (IMAGE_TEXTURES[p_bindImageTexture_0_] == p_bindImageTexture_1_)
            {
                return;
            }

            IMAGE_TEXTURES[p_bindImageTexture_0_] = p_bindImageTexture_1_;
        }

        GL42.glBindImageTexture(p_bindImageTexture_0_, p_bindImageTexture_1_, p_bindImageTexture_2_, p_bindImageTexture_3_, p_bindImageTexture_4_, p_bindImageTexture_5_, p_bindImageTexture_6_);
    }

    
    static class AlphaState
    {
        public final GlStateManager.BooleanState test = new GlStateManager.BooleanState(3008);
        public int func = 519;
        public float ref = -1.0F;

        private AlphaState()
        {
        }
    }

    static class BlendState
    {
        public final GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
        public int srcFactorRgb = 1;
        public int dstFactorRgb = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        private BlendState()
        {
        }
    }

    static class BooleanState
    {
        private final int capability;
        private boolean currentState;

        public BooleanState(int capability)
        {
            this.capability = capability;
        }

        public void disable()
        {
            this.setEnabled(false);
        }

        public void enable()
        {
            this.setEnabled(true);
        }

        public void setEnabled(boolean enabled)
        {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

            if (enabled != this.currentState)
            {
                this.currentState = enabled;

                if (enabled)
                {
                    GL11.glEnable(this.capability);
                }
                else
                {
                    GL11.glDisable(this.capability);
                }
            }
        }
    }

    
    static class Color
    {
        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public Color()
        {
            this(1.0F, 1.0F, 1.0F, 1.0F);
        }

        public Color(float red, float green, float blue, float alpha)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }

    static class ColorLogicState
    {
        public final GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
        public int logicOpcode = 5379;

        private ColorLogicState()
        {
        }
    }

    static class ColorMask
    {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask()
        {
        }
    }

    
    static class ColorMaterialState
    {
        public final GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState()
        {
        }
    }

    static class CullState
    {
        public final GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
        public int mode = 1029;

        private CullState()
        {
        }
    }

    static class DepthState
    {
        public final GlStateManager.BooleanState test = new GlStateManager.BooleanState(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthState()
        {
        }
    }

    public static enum DestFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int param;

        private DestFactor(int param)
        {
            this.param = param;
        }
    }

    
    public static enum FogMode
    {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int param;

        private FogMode(int param)
        {
            this.param = param;
        }
    }

    
    static class FogState
    {
        public final GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
        public int mode = 2048;
        public float density = 1.0F;
        public float start;
        public float end = 1.0F;

        private FogState()
        {
        }
    }

    public static enum FramebufferExtension
    {
        BASE,
        ARB,
        EXT;
    }

    public static enum LogicOp
    {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int opCode)
        {
            this.opcode = opCode;
        }
    }

    static class PolygonOffsetState
    {
        public final GlStateManager.BooleanState polyOffset = new GlStateManager.BooleanState(32823);
        public final GlStateManager.BooleanState lineOffset = new GlStateManager.BooleanState(10754);
        public float factor;
        public float units;

        private PolygonOffsetState()
        {
        }
    }

    static class ScissorState
    {
        public final GlStateManager.BooleanState field_244595_a = new GlStateManager.BooleanState(3089);

        private ScissorState()
        {
        }
    }

    public static enum SourceFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int param;

        private SourceFactor(int param)
        {
            this.param = param;
        }
    }

    static class StencilFunc
    {
        public int func = 519;
        public int ref;
        public int mask = -1;

        private StencilFunc()
        {
        }
    }

    static class StencilState
    {
        public final GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
        public int mask = -1;
        public int sfail = 7680;
        public int dpfail = 7680;
        public int dppass = 7680;

        private StencilState()
        {
        }
    }

    public static enum SupportType
    {
        BASE,
        EXT,
        NONE;
    }

    
    public static enum TexGen
    {
        S,
        T,
        R,
        Q;
    }

    
    static class TexGenCoord
    {
        public final GlStateManager.BooleanState textureGen;
        public final int coord;
        public int mode = -1;

        public TexGenCoord(int coord, int textureGen)
        {
            this.coord = coord;
            this.textureGen = new GlStateManager.BooleanState(textureGen);
        }
    }

    
    static class TexGenState
    {
        public final GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
        public final GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
        public final GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
        public final GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

        private TexGenState()
        {
        }
    }

    static class TextureState
    {
        public final GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
        public int textureName;

        private TextureState()
        {
        }
    }

    public static enum Viewport
    {
        INSTANCE;

        protected int x;
        protected int y;
        protected int w;
        protected int h;
    }
}
