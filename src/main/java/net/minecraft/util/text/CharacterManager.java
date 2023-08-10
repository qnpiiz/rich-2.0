package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ICharacterConsumer;
import net.minecraft.util.IReorderingProcessor;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class CharacterManager
{
    private final CharacterManager.ICharWidthProvider field_238347_a_;

    public CharacterManager(CharacterManager.ICharWidthProvider p_i232243_1_)
    {
        this.field_238347_a_ = p_i232243_1_;
    }

    public float func_238350_a_(@Nullable String p_238350_1_)
    {
        if (p_238350_1_ == null)
        {
            return 0.0F;
        }
        else
        {
            MutableFloat mutablefloat = new MutableFloat();
            TextProcessing.func_238346_c_(p_238350_1_, Style.EMPTY, (p_238363_2_, p_238363_3_, p_238363_4_) ->
            {
                mutablefloat.add(this.field_238347_a_.getWidth(p_238363_4_, p_238363_3_));
                return true;
            });
            return mutablefloat.floatValue();
        }
    }

    public float func_238356_a_(ITextProperties p_238356_1_)
    {
        MutableFloat mutablefloat = new MutableFloat();
        TextProcessing.func_238343_a_(p_238356_1_, Style.EMPTY, (p_238359_2_, p_238359_3_, p_238359_4_) ->
        {
            mutablefloat.add(this.field_238347_a_.getWidth(p_238359_4_, p_238359_3_));
            return true;
        });
        return mutablefloat.floatValue();
    }

    public float func_243238_a(IReorderingProcessor p_243238_1_)
    {
        MutableFloat mutablefloat = new MutableFloat();
        p_243238_1_.accept((p_243243_2_, p_243243_3_, p_243243_4_) ->
        {
            mutablefloat.add(this.field_238347_a_.getWidth(p_243243_4_, p_243243_3_));
            return true;
        });
        return mutablefloat.floatValue();
    }

    public int func_238352_a_(String p_238352_1_, int p_238352_2_, Style p_238352_3_)
    {
        CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238352_2_);
        TextProcessing.func_238341_a_(p_238352_1_, p_238352_3_, charactermanager$stringwidthprocessor);
        return charactermanager$stringwidthprocessor.func_238398_a_();
    }

    public String func_238361_b_(String p_238361_1_, int p_238361_2_, Style p_238361_3_)
    {
        return p_238361_1_.substring(0, this.func_238352_a_(p_238361_1_, p_238361_2_, p_238361_3_));
    }

    public String func_238364_c_(String p_238364_1_, int p_238364_2_, Style p_238364_3_)
    {
        MutableFloat mutablefloat = new MutableFloat();
        MutableInt mutableint = new MutableInt(p_238364_1_.length());
        TextProcessing.func_238345_b_(p_238364_1_, p_238364_3_, (p_238360_4_, p_238360_5_, p_238360_6_) ->
        {
            float f = mutablefloat.addAndGet(this.field_238347_a_.getWidth(p_238360_6_, p_238360_5_));

            if (f > (float)p_238364_2_)
            {
                return false;
            }
            else {
                mutableint.setValue(p_238360_4_);
                return true;
            }
        });
        return p_238364_1_.substring(mutableint.intValue());
    }

    @Nullable
    public Style func_238357_a_(ITextProperties p_238357_1_, int p_238357_2_)
    {
        CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238357_2_);
        return p_238357_1_.getComponentWithStyle((p_238348_1_, p_238348_2_) ->
        {
            return TextProcessing.func_238346_c_(p_238348_2_, p_238348_1_, charactermanager$stringwidthprocessor) ? Optional.empty() : Optional.of(p_238348_1_);
        }, Style.EMPTY).orElse((Style)null);
    }

    @Nullable
    public Style func_243239_a(IReorderingProcessor p_243239_1_, int p_243239_2_)
    {
        CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_243239_2_);
        MutableObject<Style> mutableobject = new MutableObject<>();
        p_243239_1_.accept((p_243240_2_, p_243240_3_, p_243240_4_) ->
        {
            if (!charactermanager$stringwidthprocessor.accept(p_243240_2_, p_243240_3_, p_243240_4_))
            {
                mutableobject.setValue(p_243240_3_);
                return false;
            }
            else {
                return true;
            }
        });
        return mutableobject.getValue();
    }

    public ITextProperties func_238358_a_(ITextProperties p_238358_1_, int p_238358_2_, Style p_238358_3_)
    {
        final CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238358_2_);
        return p_238358_1_.getComponentWithStyle(new ITextProperties.IStyledTextAcceptor<ITextProperties>()
        {
            private final TextPropertiesManager field_238368_c_ = new TextPropertiesManager();
            public Optional<ITextProperties> accept(Style p_accept_1_, String p_accept_2_)
            {
                charactermanager$stringwidthprocessor.func_238399_b_();

                if (!TextProcessing.func_238346_c_(p_accept_2_, p_accept_1_, charactermanager$stringwidthprocessor))
                {
                    String s = p_accept_2_.substring(0, charactermanager$stringwidthprocessor.func_238398_a_());

                    if (!s.isEmpty())
                    {
                        this.field_238368_c_.func_238155_a_(ITextProperties.func_240653_a_(s, p_accept_1_));
                    }

                    return Optional.of(this.field_238368_c_.func_238156_b_());
                }
                else
                {
                    if (!p_accept_2_.isEmpty())
                    {
                        this.field_238368_c_.func_238155_a_(ITextProperties.func_240653_a_(p_accept_2_, p_accept_1_));
                    }

                    return Optional.empty();
                }
            }
        }, p_238358_3_).orElse(p_238358_1_);
    }

    public static int func_238351_a_(String p_238351_0_, int p_238351_1_, int p_238351_2_, boolean p_238351_3_)
    {
        int i = p_238351_2_;
        boolean flag = p_238351_1_ < 0;
        int j = Math.abs(p_238351_1_);

        for (int k = 0; k < j; ++k)
        {
            if (flag)
            {
                while (p_238351_3_ && i > 0 && (p_238351_0_.charAt(i - 1) == ' ' || p_238351_0_.charAt(i - 1) == '\n'))
                {
                    --i;
                }

                while (i > 0 && p_238351_0_.charAt(i - 1) != ' ' && p_238351_0_.charAt(i - 1) != '\n')
                {
                    --i;
                }
            }
            else
            {
                int l = p_238351_0_.length();
                int i1 = p_238351_0_.indexOf(32, i);
                int j1 = p_238351_0_.indexOf(10, i);

                if (i1 == -1 && j1 == -1)
                {
                    i = -1;
                }
                else if (i1 != -1 && j1 != -1)
                {
                    i = Math.min(i1, j1);
                }
                else if (i1 != -1)
                {
                    i = i1;
                }
                else
                {
                    i = j1;
                }

                if (i == -1)
                {
                    i = l;
                }
                else
                {
                    while (p_238351_3_ && i < l && (p_238351_0_.charAt(i) == ' ' || p_238351_0_.charAt(i) == '\n'))
                    {
                        ++i;
                    }
                }
            }
        }

        return i;
    }

    public void func_238353_a_(String p_238353_1_, int p_238353_2_, Style p_238353_3_, boolean p_238353_4_, CharacterManager.ISliceAcceptor p_238353_5_)
    {
        int i = 0;
        int j = p_238353_1_.length();
        CharacterManager.MultilineProcessor charactermanager$multilineprocessor;

        for (Style style = p_238353_3_; i < j; style = charactermanager$multilineprocessor.func_238389_b_())
        {
            charactermanager$multilineprocessor = new CharacterManager.MultilineProcessor((float)p_238353_2_);
            boolean flag = TextProcessing.func_238340_a_(p_238353_1_, i, style, p_238353_3_, charactermanager$multilineprocessor);

            if (flag)
            {
                p_238353_5_.accept(style, i, j);
                break;
            }

            int k = charactermanager$multilineprocessor.func_238386_a_();
            char c0 = p_238353_1_.charAt(k);
            int l = c0 != '\n' && c0 != ' ' ? k : k + 1;
            p_238353_5_.accept(style, i, p_238353_4_ ? l : k);
            i = l;
        }
    }

    public List<ITextProperties> func_238365_g_(String p_238365_1_, int p_238365_2_, Style p_238365_3_)
    {
        List<ITextProperties> list = Lists.newArrayList();
        this.func_238353_a_(p_238365_1_, p_238365_2_, p_238365_3_, false, (p_238354_2_, p_238354_3_, p_238354_4_) ->
        {
            list.add(ITextProperties.func_240653_a_(p_238365_1_.substring(p_238354_3_, p_238354_4_), p_238354_2_));
        });
        return list;
    }

    public List<ITextProperties> func_238362_b_(ITextProperties p_238362_1_, int p_238362_2_, Style p_238362_3_)
    {
        List<ITextProperties> list = Lists.newArrayList();
        this.func_243242_a(p_238362_1_, p_238362_2_, p_238362_3_, (p_243241_1_, p_243241_2_) ->
        {
            list.add(p_243241_1_);
        });
        return list;
    }

    public void func_243242_a(ITextProperties p_243242_1_, int p_243242_2_, Style p_243242_3_, BiConsumer<ITextProperties, Boolean> p_243242_4_)
    {
        List<CharacterManager.StyleOverridingTextComponent> list = Lists.newArrayList();
        p_243242_1_.getComponentWithStyle((p_238355_1_, p_238355_2_) ->
        {
            if (!p_238355_2_.isEmpty())
            {
                list.add(new CharacterManager.StyleOverridingTextComponent(p_238355_2_, p_238355_1_));
            }

            return Optional.empty();
        }, p_243242_3_);
        CharacterManager.SubstyledText charactermanager$substyledtext = new CharacterManager.SubstyledText(list);
        boolean flag = true;
        boolean flag1 = false;
        boolean flag2 = false;

        while (flag)
        {
            flag = false;
            CharacterManager.MultilineProcessor charactermanager$multilineprocessor = new CharacterManager.MultilineProcessor((float)p_243242_2_);

            for (CharacterManager.StyleOverridingTextComponent charactermanager$styleoverridingtextcomponent : charactermanager$substyledtext.field_238369_a_)
            {
                boolean flag3 = TextProcessing.func_238340_a_(charactermanager$styleoverridingtextcomponent.field_238391_a_, 0, charactermanager$styleoverridingtextcomponent.field_238392_d_, p_243242_3_, charactermanager$multilineprocessor);

                if (!flag3)
                {
                    int i = charactermanager$multilineprocessor.func_238386_a_();
                    Style style = charactermanager$multilineprocessor.func_238389_b_();
                    char c0 = charactermanager$substyledtext.func_238372_a_(i);
                    boolean flag4 = c0 == '\n';
                    boolean flag5 = flag4 || c0 == ' ';
                    flag1 = flag4;
                    ITextProperties itextproperties = charactermanager$substyledtext.func_238373_a_(i, flag5 ? 1 : 0, style);
                    p_243242_4_.accept(itextproperties, flag2);
                    flag2 = !flag4;
                    flag = true;
                    break;
                }

                charactermanager$multilineprocessor.func_238387_a_(charactermanager$styleoverridingtextcomponent.field_238391_a_.length());
            }
        }

        ITextProperties itextproperties1 = charactermanager$substyledtext.func_238371_a_();

        if (itextproperties1 != null)
        {
            p_243242_4_.accept(itextproperties1, flag2);
        }
        else if (flag1)
        {
            p_243242_4_.accept(ITextProperties.field_240651_c_, false);
        }
    }

    @FunctionalInterface
    public interface ICharWidthProvider
    {
        float getWidth(int p_getWidth_1_, Style p_getWidth_2_);
    }

    @FunctionalInterface
    public interface ISliceAcceptor
    {
        void accept(Style p_accept_1_, int p_accept_2_, int p_accept_3_);
    }

    class MultilineProcessor implements ICharacterConsumer
    {
        private final float field_238377_b_;
        private int field_238378_c_ = -1;
        private Style field_238379_d_ = Style.EMPTY;
        private boolean field_238380_e_;
        private float field_238381_f_;
        private int field_238382_g_ = -1;
        private Style field_238383_h_ = Style.EMPTY;
        private int field_238384_i_;
        private int field_238385_j_;

        public MultilineProcessor(float p_i232246_2_)
        {
            this.field_238377_b_ = Math.max(p_i232246_2_, 1.0F);
        }

        public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_)
        {
            int i = p_accept_1_ + this.field_238385_j_;

            switch (p_accept_3_)
            {
                case 10:
                    return this.func_238388_a_(i, p_accept_2_);

                case 32:
                    this.field_238382_g_ = i;
                    this.field_238383_h_ = p_accept_2_;

                default:
                    float f = CharacterManager.this.field_238347_a_.getWidth(p_accept_3_, p_accept_2_);
                    this.field_238381_f_ += f;

                    if (this.field_238380_e_ && this.field_238381_f_ > this.field_238377_b_)
                    {
                        return this.field_238382_g_ != -1 ? this.func_238388_a_(this.field_238382_g_, this.field_238383_h_) : this.func_238388_a_(i, p_accept_2_);
                    }
                    else
                    {
                        this.field_238380_e_ |= f != 0.0F;
                        this.field_238384_i_ = i + Character.charCount(p_accept_3_);
                        return true;
                    }
            }
        }

        private boolean func_238388_a_(int p_238388_1_, Style p_238388_2_)
        {
            this.field_238378_c_ = p_238388_1_;
            this.field_238379_d_ = p_238388_2_;
            return false;
        }

        private boolean func_238390_c_()
        {
            return this.field_238378_c_ != -1;
        }

        public int func_238386_a_()
        {
            return this.func_238390_c_() ? this.field_238378_c_ : this.field_238384_i_;
        }

        public Style func_238389_b_()
        {
            return this.field_238379_d_;
        }

        public void func_238387_a_(int p_238387_1_)
        {
            this.field_238385_j_ += p_238387_1_;
        }
    }

    class StringWidthProcessor implements ICharacterConsumer
    {
        private float field_238396_b_;
        private int field_238397_c_;

        public StringWidthProcessor(float p_i232248_2_)
        {
            this.field_238396_b_ = p_i232248_2_;
        }

        public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_)
        {
            this.field_238396_b_ -= CharacterManager.this.field_238347_a_.getWidth(p_accept_3_, p_accept_2_);

            if (this.field_238396_b_ >= 0.0F)
            {
                this.field_238397_c_ = p_accept_1_ + Character.charCount(p_accept_3_);
                return true;
            }
            else
            {
                return false;
            }
        }

        public int func_238398_a_()
        {
            return this.field_238397_c_;
        }

        public void func_238399_b_()
        {
            this.field_238397_c_ = 0;
        }
    }

    static class StyleOverridingTextComponent implements ITextProperties
    {
        private final String field_238391_a_;
        private final Style field_238392_d_;

        public StyleOverridingTextComponent(String p_i232247_1_, Style p_i232247_2_)
        {
            this.field_238391_a_ = p_i232247_1_;
            this.field_238392_d_ = p_i232247_2_;
        }

        public <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor)
        {
            return acceptor.accept(this.field_238391_a_);
        }

        public <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn)
        {
            return acceptor.accept(this.field_238392_d_.mergeStyle(styleIn), this.field_238391_a_);
        }
    }

    static class SubstyledText
    {
        private final List<CharacterManager.StyleOverridingTextComponent> field_238369_a_;
        private String field_238370_b_;

        public SubstyledText(List<CharacterManager.StyleOverridingTextComponent> p_i232245_1_)
        {
            this.field_238369_a_ = p_i232245_1_;
            this.field_238370_b_ = p_i232245_1_.stream().map((p_238375_0_) ->
            {
                return p_238375_0_.field_238391_a_;
            }).collect(Collectors.joining());
        }

        public char func_238372_a_(int p_238372_1_)
        {
            return this.field_238370_b_.charAt(p_238372_1_);
        }

        public ITextProperties func_238373_a_(int p_238373_1_, int p_238373_2_, Style p_238373_3_)
        {
            TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
            ListIterator<CharacterManager.StyleOverridingTextComponent> listiterator = this.field_238369_a_.listIterator();
            int i = p_238373_1_;
            boolean flag = false;

            while (listiterator.hasNext())
            {
                CharacterManager.StyleOverridingTextComponent charactermanager$styleoverridingtextcomponent = listiterator.next();
                String s = charactermanager$styleoverridingtextcomponent.field_238391_a_;
                int j = s.length();

                if (!flag)
                {
                    if (i > j)
                    {
                        textpropertiesmanager.func_238155_a_(charactermanager$styleoverridingtextcomponent);
                        listiterator.remove();
                        i -= j;
                    }
                    else
                    {
                        String s1 = s.substring(0, i);

                        if (!s1.isEmpty())
                        {
                            textpropertiesmanager.func_238155_a_(ITextProperties.func_240653_a_(s1, charactermanager$styleoverridingtextcomponent.field_238392_d_));
                        }

                        i += p_238373_2_;
                        flag = true;
                    }
                }

                if (flag)
                {
                    if (i <= j)
                    {
                        String s2 = s.substring(i);

                        if (s2.isEmpty())
                        {
                            listiterator.remove();
                        }
                        else
                        {
                            listiterator.set(new CharacterManager.StyleOverridingTextComponent(s2, p_238373_3_));
                        }

                        break;
                    }

                    listiterator.remove();
                    i -= j;
                }
            }

            this.field_238370_b_ = this.field_238370_b_.substring(p_238373_1_ + p_238373_2_);
            return textpropertiesmanager.func_238156_b_();
        }

        @Nullable
        public ITextProperties func_238371_a_()
        {
            TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
            this.field_238369_a_.forEach(textpropertiesmanager::func_238155_a_);
            this.field_238369_a_.clear();
            return textpropertiesmanager.func_238154_a_();
        }
    }
}
