package net.optifine.render;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;

public class RenderStateManager
{
    private static boolean cacheEnabled;
    private static final RenderState[] PENDING_CLEAR_STATES = new RenderState[RenderType.getCountRenderStates()];

    public static void setupRenderStates(List<RenderState> renderStates)
    {
        if (cacheEnabled)
        {
            setupCached(renderStates);
        }
        else
        {
            for (int i = 0; i < renderStates.size(); ++i)
            {
                RenderState renderstate = renderStates.get(i);
                renderstate.setupRenderState();
            }
        }
    }

    public static void clearRenderStates(List<RenderState> renderStates)
    {
        if (cacheEnabled)
        {
            clearCached(renderStates);
        }
        else
        {
            for (int i = 0; i < renderStates.size(); ++i)
            {
                RenderState renderstate = renderStates.get(i);
                renderstate.clearRenderState();
            }
        }
    }

    private static void setupCached(List<RenderState> renderStates)
    {
        for (int i = 0; i < renderStates.size(); ++i)
        {
            RenderState renderstate = renderStates.get(i);
            setupCached(renderstate, i);
        }
    }

    private static void clearCached(List<RenderState> renderStates)
    {
        for (int i = 0; i < renderStates.size(); ++i)
        {
            RenderState renderstate = renderStates.get(i);
            clearCached(renderstate, i);
        }
    }

    private static void setupCached(RenderState state, int index)
    {
        RenderState renderstate = PENDING_CLEAR_STATES[index];

        if (renderstate != null)
        {
            if (state == renderstate)
            {
                PENDING_CLEAR_STATES[index] = null;
                return;
            }

            renderstate.clearRenderState();
            PENDING_CLEAR_STATES[index] = null;
        }

        state.setupRenderState();
    }

    private static void clearCached(RenderState state, int index)
    {
        RenderState renderstate = PENDING_CLEAR_STATES[index];

        if (renderstate != null)
        {
            renderstate.clearRenderState();
        }

        PENDING_CLEAR_STATES[index] = state;
    }

    public static void enableCache()
    {
        if (!cacheEnabled)
        {
            cacheEnabled = true;
            Arrays.fill(PENDING_CLEAR_STATES, (Object)null);
        }
    }

    public static void disableCache()
    {
        if (cacheEnabled)
        {
            cacheEnabled = false;

            for (int i = 0; i < PENDING_CLEAR_STATES.length; ++i)
            {
                RenderState renderstate = PENDING_CLEAR_STATES[i];

                if (renderstate != null)
                {
                    renderstate.clearRenderState();
                }
            }

            Arrays.fill(PENDING_CLEAR_STATES, (Object)null);
        }
    }
}
