package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandSuggestionHelper
{
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style EMPTY_ERROR_STYLE = Style.EMPTY.setFormatting(TextFormatting.RED);
    private static final Style EMPTY_PASS_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);
    private static final List<Style> COMMAND_COLOR_STYLES = Stream.of(TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD).map(Style.EMPTY::setFormatting).collect(ImmutableList.toImmutableList());
    private final Minecraft mc;
    private final Screen screen;
    private final TextFieldWidget inputField;
    private final FontRenderer font;
    private final boolean commandsOnly;
    private final boolean hasCursor;
    private final int minAmountRendered;
    private final int maxAmountRendered;
    private final boolean isChat;
    private final int color;
    private final List<IReorderingProcessor> exceptionList = Lists.newArrayList();
    private int x;
    private int width;
    private ParseResults<ISuggestionProvider> parseResults;
    private CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestionsFuture;
    private CommandSuggestionHelper.Suggestions suggestions;
    private boolean autoSuggest;
    private boolean isApplyingSuggestion;

    public CommandSuggestionHelper(Minecraft mc, Screen screen, TextFieldWidget inputField, FontRenderer font, boolean commandsOnly, boolean hasCursor, int minAmountRendered, int maxAmountRendered, boolean isChat, int color)
    {
        this.mc = mc;
        this.screen = screen;
        this.inputField = inputField;
        this.font = font;
        this.commandsOnly = commandsOnly;
        this.hasCursor = hasCursor;
        this.minAmountRendered = minAmountRendered;
        this.maxAmountRendered = maxAmountRendered;
        this.isChat = isChat;
        this.color = color;
        inputField.setTextFormatter(this::getParsedSuggestion);
    }

    public void shouldAutoSuggest(boolean autoSuggest)
    {
        this.autoSuggest = autoSuggest;

        if (!autoSuggest)
        {
            this.suggestions = null;
        }
    }

    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.suggestions != null && this.suggestions.onKeyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (this.screen.getListener() == this.inputField && keyCode == 258)
        {
            this.updateSuggestions(true);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean onScroll(double delta)
    {
        return this.suggestions != null && this.suggestions.onScroll(MathHelper.clamp(delta, -1.0D, 1.0D));
    }

    public boolean onClick(double mouseX, double mouseY, int mouseButton)
    {
        return this.suggestions != null && this.suggestions.onClick((int)mouseX, (int)mouseY, mouseButton);
    }

    public void updateSuggestions(boolean narrateFirstSuggestion)
    {
        if (this.suggestionsFuture != null && this.suggestionsFuture.isDone())
        {
            com.mojang.brigadier.suggestion.Suggestions suggestions = this.suggestionsFuture.join();

            if (!suggestions.isEmpty())
            {
                int i = 0;

                for (Suggestion suggestion : suggestions.getList())
                {
                    i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
                }

                int j = MathHelper.clamp(this.inputField.func_195611_j(suggestions.getRange().getStart()), 0, this.inputField.func_195611_j(0) + this.inputField.getAdjustedWidth() - i);
                int k = this.isChat ? this.screen.height - 12 : 72;
                this.suggestions = new CommandSuggestionHelper.Suggestions(j, k, i, this.getSuggestions(suggestions), narrateFirstSuggestion);
            }
        }
    }

    private List<Suggestion> getSuggestions(com.mojang.brigadier.suggestion.Suggestions suggestions)
    {
        String s = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
        int i = getLastWhitespace(s);
        String s1 = s.substring(i).toLowerCase(Locale.ROOT);
        List<Suggestion> list = Lists.newArrayList();
        List<Suggestion> list1 = Lists.newArrayList();

        for (Suggestion suggestion : suggestions.getList())
        {
            if (!suggestion.getText().startsWith(s1) && !suggestion.getText().startsWith("minecraft:" + s1))
            {
                list1.add(suggestion);
            }
            else
            {
                list.add(suggestion);
            }
        }

        list.addAll(list1);
        return list;
    }

    public void init()
    {
        String s = this.inputField.getText();

        if (this.parseResults != null && !this.parseResults.getReader().getString().equals(s))
        {
            this.parseResults = null;
        }

        if (!this.isApplyingSuggestion)
        {
            this.inputField.setSuggestion((String)null);
            this.suggestions = null;
        }

        this.exceptionList.clear();
        StringReader stringreader = new StringReader(s);
        boolean flag = stringreader.canRead() && stringreader.peek() == '/';

        if (flag)
        {
            stringreader.skip();
        }

        boolean flag1 = this.commandsOnly || flag;
        int i = this.inputField.getCursorPosition();

        if (flag1)
        {
            CommandDispatcher<ISuggestionProvider> commanddispatcher = this.mc.player.connection.getCommandDispatcher();

            if (this.parseResults == null)
            {
                this.parseResults = commanddispatcher.parse(stringreader, this.mc.player.connection.getSuggestionProvider());
            }

            int j = this.hasCursor ? stringreader.getCursor() : 1;

            if (i >= j && (this.suggestions == null || !this.isApplyingSuggestion))
            {
                this.suggestionsFuture = commanddispatcher.getCompletionSuggestions(this.parseResults, i);
                this.suggestionsFuture.thenRun(() ->
                {
                    if (this.suggestionsFuture.isDone())
                    {
                        this.recompileSuggestions();
                    }
                });
            }
        }
        else
        {
            String s1 = s.substring(0, i);
            int k = getLastWhitespace(s1);
            Collection<String> collection = this.mc.player.connection.getSuggestionProvider().getPlayerNames();
            this.suggestionsFuture = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
        }
    }

    private static int getLastWhitespace(String text)
    {
        if (Strings.isNullOrEmpty(text))
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (Matcher matcher = WHITESPACE_PATTERN.matcher(text); matcher.find(); i = matcher.end())
            {
            }

            return i;
        }
    }

    private static IReorderingProcessor func_243255_a(CommandSyntaxException exception)
    {
        ITextComponent itextcomponent = TextComponentUtils.toTextComponent(exception.getRawMessage());
        String s = exception.getContext();
        return s == null ? itextcomponent.func_241878_f() : (new TranslationTextComponent("command.context.parse_error", itextcomponent, exception.getCursor(), s)).func_241878_f();
    }

    private void recompileSuggestions()
    {
        if (this.inputField.getCursorPosition() == this.inputField.getText().length())
        {
            if (this.suggestionsFuture.join().isEmpty() && !this.parseResults.getExceptions().isEmpty())
            {
                int i = 0;

                for (Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.parseResults.getExceptions().entrySet())
                {
                    CommandSyntaxException commandsyntaxexception = entry.getValue();

                    if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect())
                    {
                        ++i;
                    }
                    else
                    {
                        this.exceptionList.add(func_243255_a(commandsyntaxexception));
                    }
                }

                if (i > 0)
                {
                    this.exceptionList.add(func_243255_a(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            }
            else if (this.parseResults.getReader().canRead())
            {
                this.exceptionList.add(func_243255_a(Commands.func_227481_a_(this.parseResults)));
            }
        }

        this.x = 0;
        this.width = this.screen.width;

        if (this.exceptionList.isEmpty())
        {
            this.applyFormattingToCommand(TextFormatting.GRAY);
        }

        this.suggestions = null;

        if (this.autoSuggest && this.mc.gameSettings.autoSuggestCommands)
        {
            this.updateSuggestions(false);
        }
    }

    private void applyFormattingToCommand(TextFormatting formatting)
    {
        CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.parseResults.getContext();
        SuggestionContext<ISuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.inputField.getCursorPosition());
        Map<CommandNode<ISuggestionProvider>, String> map = this.mc.player.connection.getCommandDispatcher().getSmartUsage(suggestioncontext.parent, this.mc.player.connection.getSuggestionProvider());
        List<IReorderingProcessor> list = Lists.newArrayList();
        int i = 0;
        Style style = Style.EMPTY.setFormatting(formatting);

        for (Entry<CommandNode<ISuggestionProvider>, String> entry : map.entrySet())
        {
            if (!(entry.getKey() instanceof LiteralCommandNode))
            {
                list.add(IReorderingProcessor.fromString(entry.getValue(), style));
                i = Math.max(i, this.font.getStringWidth(entry.getValue()));
            }
        }

        if (!list.isEmpty())
        {
            this.exceptionList.addAll(list);
            this.x = MathHelper.clamp(this.inputField.func_195611_j(suggestioncontext.startPos), 0, this.inputField.func_195611_j(0) + this.inputField.getAdjustedWidth() - i);
            this.width = i;
        }
    }

    private IReorderingProcessor getParsedSuggestion(String command, int maxLength)
    {
        return this.parseResults != null ? getFinalSuggestion(this.parseResults, command, maxLength) : IReorderingProcessor.fromString(command, Style.EMPTY);
    }

    @Nullable
    private static String getMatchedSuggestionText(String inputText, String suggestionText)
    {
        return suggestionText.startsWith(inputText) ? suggestionText.substring(inputText.length()) : null;
    }

    private static IReorderingProcessor getFinalSuggestion(ParseResults<ISuggestionProvider> provider, String command, int maxLength)
    {
        List<IReorderingProcessor> list = Lists.newArrayList();
        int i = 0;
        int j = -1;
        CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = provider.getContext().getLastChild();

        for (ParsedArgument < ISuggestionProvider, ? > parsedargument : commandcontextbuilder.getArguments().values())
        {
            ++j;

            if (j >= COMMAND_COLOR_STYLES.size())
            {
                j = 0;
            }

            int k = Math.max(parsedargument.getRange().getStart() - maxLength, 0);

            if (k >= command.length())
            {
                break;
            }

            int l = Math.min(parsedargument.getRange().getEnd() - maxLength, command.length());

            if (l > 0)
            {
                list.add(IReorderingProcessor.fromString(command.substring(i, k), EMPTY_PASS_STYLE));
                list.add(IReorderingProcessor.fromString(command.substring(k, l), COMMAND_COLOR_STYLES.get(j)));
                i = l;
            }
        }

        if (provider.getReader().canRead())
        {
            int i1 = Math.max(provider.getReader().getCursor() - maxLength, 0);

            if (i1 < command.length())
            {
                int j1 = Math.min(i1 + provider.getReader().getRemainingLength(), command.length());
                list.add(IReorderingProcessor.fromString(command.substring(i, i1), EMPTY_PASS_STYLE));
                list.add(IReorderingProcessor.fromString(command.substring(i1, j1), EMPTY_ERROR_STYLE));
                i = j1;
            }
        }

        list.add(IReorderingProcessor.fromString(command.substring(i), EMPTY_PASS_STYLE));
        return IReorderingProcessor.func_242241_a(list);
    }

    public void drawSuggestionList(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (this.suggestions != null)
        {
            this.suggestions.drawSuggestions(matrixStack, mouseX, mouseY);
        }
        else
        {
            int i = 0;

            for (IReorderingProcessor ireorderingprocessor : this.exceptionList)
            {
                int j = this.isChat ? this.screen.height - 14 - 13 - 12 * i : 72 + 12 * i;
                AbstractGui.fill(matrixStack, this.x - 1, j, this.x + this.width + 1, j + 12, this.color);
                this.font.func_238407_a_(matrixStack, ireorderingprocessor, (float)this.x, (float)(j + 2), -1);
                ++i;
            }
        }
    }

    public String getSuggestionMessage()
    {
        return this.suggestions != null ? "\n" + this.suggestions.getCurrentSuggestionMessage() : "";
    }

    public class Suggestions
    {
        private final Rectangle2d suggestionRenderBox;
        private final String originalInputText;
        private final List<Suggestion> suggestions;
        private int lowestDisplayedSuggestionIndex;
        private int selectedIndex;
        private Vector2f lastMousePosition = Vector2f.ZERO;
        private boolean changeSelectionOnNextTabInput;
        private int lastObtainedSuggestionMessageIndex;

        private Suggestions(int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion)
        {
            int i = x - 1;
            int j = CommandSuggestionHelper.this.isChat ? y - 3 - Math.min(suggestions.size(), CommandSuggestionHelper.this.maxAmountRendered) * 12 : y;
            this.suggestionRenderBox = new Rectangle2d(i, j, width + 1, Math.min(suggestions.size(), CommandSuggestionHelper.this.maxAmountRendered) * 12);
            this.originalInputText = CommandSuggestionHelper.this.inputField.getText();
            this.lastObtainedSuggestionMessageIndex = narrateFirstSuggestion ? -1 : 0;
            this.suggestions = suggestions;
            this.selectSuggestion(0);
        }

        public void drawSuggestions(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            int i = Math.min(this.suggestions.size(), CommandSuggestionHelper.this.maxAmountRendered);
            int j = -5592406;
            boolean flag = this.lowestDisplayedSuggestionIndex > 0;
            boolean flag1 = this.suggestions.size() > this.lowestDisplayedSuggestionIndex + i;
            boolean flag2 = flag || flag1;
            boolean flag3 = this.lastMousePosition.x != (float)mouseX || this.lastMousePosition.y != (float)mouseY;

            if (flag3)
            {
                this.lastMousePosition = new Vector2f((float)mouseX, (float)mouseY);
            }

            if (flag2)
            {
                AbstractGui.fill(matrixStack, this.suggestionRenderBox.getX(), this.suggestionRenderBox.getY() - 1, this.suggestionRenderBox.getX() + this.suggestionRenderBox.getWidth(), this.suggestionRenderBox.getY(), CommandSuggestionHelper.this.color);
                AbstractGui.fill(matrixStack, this.suggestionRenderBox.getX(), this.suggestionRenderBox.getY() + this.suggestionRenderBox.getHeight(), this.suggestionRenderBox.getX() + this.suggestionRenderBox.getWidth(), this.suggestionRenderBox.getY() + this.suggestionRenderBox.getHeight() + 1, CommandSuggestionHelper.this.color);

                if (flag)
                {
                    for (int k = 0; k < this.suggestionRenderBox.getWidth(); ++k)
                    {
                        if (k % 2 == 0)
                        {
                            AbstractGui.fill(matrixStack, this.suggestionRenderBox.getX() + k, this.suggestionRenderBox.getY() - 1, this.suggestionRenderBox.getX() + k + 1, this.suggestionRenderBox.getY(), -1);
                        }
                    }
                }

                if (flag1)
                {
                    for (int i1 = 0; i1 < this.suggestionRenderBox.getWidth(); ++i1)
                    {
                        if (i1 % 2 == 0)
                        {
                            AbstractGui.fill(matrixStack, this.suggestionRenderBox.getX() + i1, this.suggestionRenderBox.getY() + this.suggestionRenderBox.getHeight(), this.suggestionRenderBox.getX() + i1 + 1, this.suggestionRenderBox.getY() + this.suggestionRenderBox.getHeight() + 1, -1);
                        }
                    }
                }
            }

            boolean flag4 = false;

            for (int l = 0; l < i; ++l)
            {
                Suggestion suggestion = this.suggestions.get(l + this.lowestDisplayedSuggestionIndex);
                AbstractGui.fill(matrixStack, this.suggestionRenderBox.getX(), this.suggestionRenderBox.getY() + 12 * l, this.suggestionRenderBox.getX() + this.suggestionRenderBox.getWidth(), this.suggestionRenderBox.getY() + 12 * l + 12, CommandSuggestionHelper.this.color);

                if (mouseX > this.suggestionRenderBox.getX() && mouseX < this.suggestionRenderBox.getX() + this.suggestionRenderBox.getWidth() && mouseY > this.suggestionRenderBox.getY() + 12 * l && mouseY < this.suggestionRenderBox.getY() + 12 * l + 12)
                {
                    if (flag3)
                    {
                        this.selectSuggestion(l + this.lowestDisplayedSuggestionIndex);
                    }

                    flag4 = true;
                }

                CommandSuggestionHelper.this.font.drawStringWithShadow(matrixStack, suggestion.getText(), (float)(this.suggestionRenderBox.getX() + 1), (float)(this.suggestionRenderBox.getY() + 2 + 12 * l), l + this.lowestDisplayedSuggestionIndex == this.selectedIndex ? -256 : -5592406);
            }

            if (flag4)
            {
                Message message = this.suggestions.get(this.selectedIndex).getTooltip();

                if (message != null)
                {
                    CommandSuggestionHelper.this.screen.renderTooltip(matrixStack, TextComponentUtils.toTextComponent(message), mouseX, mouseY);
                }
            }
        }

        public boolean onClick(int mouseX, int mouseY, int mouseButton)
        {
            if (!this.suggestionRenderBox.contains(mouseX, mouseY))
            {
                return false;
            }
            else
            {
                int i = (mouseY - this.suggestionRenderBox.getY()) / 12 + this.lowestDisplayedSuggestionIndex;

                if (i >= 0 && i < this.suggestions.size())
                {
                    this.selectSuggestion(i);
                    this.applySuggestionToInput();
                }

                return true;
            }
        }

        public boolean onScroll(double delta)
        {
            int i = (int)(CommandSuggestionHelper.this.mc.mouseHelper.getMouseX() * (double)CommandSuggestionHelper.this.mc.getMainWindow().getScaledWidth() / (double)CommandSuggestionHelper.this.mc.getMainWindow().getWidth());
            int j = (int)(CommandSuggestionHelper.this.mc.mouseHelper.getMouseY() * (double)CommandSuggestionHelper.this.mc.getMainWindow().getScaledHeight() / (double)CommandSuggestionHelper.this.mc.getMainWindow().getHeight());

            if (this.suggestionRenderBox.contains(i, j))
            {
                this.lowestDisplayedSuggestionIndex = MathHelper.clamp((int)((double)this.lowestDisplayedSuggestionIndex - delta), 0, Math.max(this.suggestions.size() - CommandSuggestionHelper.this.maxAmountRendered, 0));
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (keyCode == 265)
            {
                this.changeSelection(-1);
                this.changeSelectionOnNextTabInput = false;
                return true;
            }
            else if (keyCode == 264)
            {
                this.changeSelection(1);
                this.changeSelectionOnNextTabInput = false;
                return true;
            }
            else if (keyCode == 258)
            {
                if (this.changeSelectionOnNextTabInput)
                {
                    this.changeSelection(Screen.hasShiftDown() ? -1 : 1);
                }

                this.applySuggestionToInput();
                return true;
            }
            else if (keyCode == 256)
            {
                this.clearSuggestions();
                return true;
            }
            else
            {
                return false;
            }
        }

        public void changeSelection(int change)
        {
            this.selectSuggestion(this.selectedIndex + change);
            int i = this.lowestDisplayedSuggestionIndex;
            int j = this.lowestDisplayedSuggestionIndex + CommandSuggestionHelper.this.maxAmountRendered - 1;

            if (this.selectedIndex < i)
            {
                this.lowestDisplayedSuggestionIndex = MathHelper.clamp(this.selectedIndex, 0, Math.max(this.suggestions.size() - CommandSuggestionHelper.this.maxAmountRendered, 0));
            }
            else if (this.selectedIndex > j)
            {
                this.lowestDisplayedSuggestionIndex = MathHelper.clamp(this.selectedIndex + CommandSuggestionHelper.this.minAmountRendered - CommandSuggestionHelper.this.maxAmountRendered, 0, Math.max(this.suggestions.size() - CommandSuggestionHelper.this.maxAmountRendered, 0));
            }
        }

        public void selectSuggestion(int index)
        {
            this.selectedIndex = index;

            if (this.selectedIndex < 0)
            {
                this.selectedIndex += this.suggestions.size();
            }

            if (this.selectedIndex >= this.suggestions.size())
            {
                this.selectedIndex -= this.suggestions.size();
            }

            Suggestion suggestion = this.suggestions.get(this.selectedIndex);
            CommandSuggestionHelper.this.inputField.setSuggestion(CommandSuggestionHelper.getMatchedSuggestionText(CommandSuggestionHelper.this.inputField.getText(), suggestion.apply(this.originalInputText)));

            if (NarratorChatListener.INSTANCE.isActive() && this.lastObtainedSuggestionMessageIndex != this.selectedIndex)
            {
                NarratorChatListener.INSTANCE.say(this.getCurrentSuggestionMessage());
            }
        }

        public void applySuggestionToInput()
        {
            Suggestion suggestion = this.suggestions.get(this.selectedIndex);
            CommandSuggestionHelper.this.isApplyingSuggestion = true;
            CommandSuggestionHelper.this.inputField.setText(suggestion.apply(this.originalInputText));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            CommandSuggestionHelper.this.inputField.clampCursorPosition(i);
            CommandSuggestionHelper.this.inputField.setSelectionPos(i);
            this.selectSuggestion(this.selectedIndex);
            CommandSuggestionHelper.this.isApplyingSuggestion = false;
            this.changeSelectionOnNextTabInput = true;
        }

        private String getCurrentSuggestionMessage()
        {
            this.lastObtainedSuggestionMessageIndex = this.selectedIndex;
            Suggestion suggestion = this.suggestions.get(this.selectedIndex);
            Message message = suggestion.getTooltip();
            return message != null ? I18n.format("narration.suggestion.tooltip", this.selectedIndex + 1, this.suggestions.size(), suggestion.getText(), message.getString()) : I18n.format("narration.suggestion", this.selectedIndex + 1, this.suggestions.size(), suggestion.getText());
        }

        public void clearSuggestions()
        {
            CommandSuggestionHelper.this.suggestions = null;
        }
    }
}
