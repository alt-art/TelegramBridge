package org.altart.telegrambridge.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.Objects;

public class ComponentMatcher implements ArgumentMatcher<BaseComponent> {
    private final BaseComponent right;

    public ComponentMatcher(BaseComponent right) {
        this.right = right;
    }

    @Override
    public boolean matches(BaseComponent left) {
        try {
            deepAssert(left, right);
        } catch (AssertionError e) {
            return false;
        }
        return true;
    }

    private void deepAssert(BaseComponent left, BaseComponent right) {
        assertPlainText(left, right);
        assertInsertion(left, right);
        assertColor(left, right);
        assertBold(left, right);
        assertItalic(left, right);
        assertUnderline(left, right);
        assertStrikethrough(left, right);
        assertObfuscated(left, right);
        assertHoverEvent(left, right);
        assertClickEvent(left, right);
        assertExtra(left, right);
    }

    private void assertExtra(BaseComponent left, BaseComponent right) {
        List<BaseComponent> leftExtra = left.getExtra();
        List<BaseComponent> rightExtra = right.getExtra();

        if (rightExtra == null) {
            if (leftExtra != null) {
                throw new AssertionError("Expected no extra but got \"" + leftExtra + "\"");
            }
        } else {
            if (leftExtra == null) {
                throw new AssertionError("Expected extra \"" + rightExtra + "\" but got none");
            }
            if (leftExtra.size() != rightExtra.size()) {
                throw new AssertionError("Expected extra size \"" + rightExtra.size() + "\" but got \"" + leftExtra.size() + "\"");
            }
            for (int i = 0; i < leftExtra.size(); i++) {
                deepAssert(leftExtra.get(i), rightExtra.get(i));
            }
        }
    }

    private void assertPlainText(BaseComponent left, BaseComponent right) {
        String leftText = left.toPlainText();
        String rightText = right.toPlainText();
        if (!rightText.equals(leftText)) {
            throw new AssertionError("Expected text \"" + rightText + "\" but got \"" + leftText + "\"");
        }
    }

    private void assertObfuscated(BaseComponent left, BaseComponent right) {
        boolean leftObfuscated = left.isObfuscated();
        boolean rightObfuscated = right.isObfuscated();
        if (leftObfuscated != rightObfuscated) {
            throw new AssertionError("Expected obfuscated \"" + rightObfuscated + "\" but got \"" + leftObfuscated + "\"");
        }
    }

    private void assertStrikethrough(BaseComponent left, BaseComponent right) {
        boolean leftStrikethrough = left.isStrikethrough();
        boolean rightStrikethrough = right.isStrikethrough();
        if (leftStrikethrough != rightStrikethrough) {
            throw new AssertionError("Expected strikethrough \"" + rightStrikethrough + "\" but got \"" + leftStrikethrough + "\"");
        }
    }

    private void assertUnderline(BaseComponent left, BaseComponent right) {
        boolean leftUnderlined = left.isUnderlined();
        boolean rightUnderlined = right.isUnderlined();
        if (leftUnderlined != rightUnderlined) {
            throw new AssertionError("Expected underlined \"" + rightUnderlined + "\" but got \"" + leftUnderlined + "\"");
        }
    }

    private void assertItalic(BaseComponent left, BaseComponent right) {
        boolean leftItalic = left.isItalic();
        boolean rightItalic = right.isItalic();
        if (leftItalic != rightItalic) {
            throw new AssertionError("Expected italic \"" + rightItalic + "\" but got \"" + leftItalic + "\"");
        }
    }

    private void assertBold(BaseComponent left, BaseComponent right) {
        boolean leftBold = left.isBold();
        boolean rightBold = right.isBold();
        if (leftBold != rightBold) {
            throw new AssertionError("Expected bold \"" + rightBold + "\" but got \"" + leftBold + "\"");
        }
    }

    private void assertColor(BaseComponent left, BaseComponent right) {
        ChatColor leftColor = left.getColor();
        ChatColor rightColor = right.getColor();
        if (!right.getColor().equals(left.getColor())) {
            throw new AssertionError("Expected color \"" + rightColor + "\" but got \"" + leftColor + "\"");
        }
    }

    private void assertClickEvent(BaseComponent left, BaseComponent right) {
        ClickEvent leftClickEvent = left.getClickEvent();
        ClickEvent rightClickEvent = right.getClickEvent();
        if (rightClickEvent == null) {
            if (leftClickEvent != null) {
                throw new AssertionError("Expected no click event but got \"" + leftClickEvent + "\"");
            }
        } else {
            if (leftClickEvent == null) {
                throw new AssertionError("Expected click event \"" + rightClickEvent + "\" but got none");
            }
            ClickEvent.Action leftClickAction = leftClickEvent.getAction();
            ClickEvent.Action rightClickAction = rightClickEvent.getAction();
            if (!Objects.equals(leftClickAction, rightClickAction)) {
                throw new AssertionError("Expected click action \"" + rightClickAction + "\" but got \"" + leftClickAction + "\"");
            }
            String leftClickValue = leftClickEvent.getValue();
            String rightClickValue = rightClickEvent.getValue();
            if (!Objects.equals(leftClickValue, rightClickValue)) {
                throw new AssertionError("Expected click value \"" + rightClickValue + "\" but got \"" + leftClickValue + "\"");
            }
        }
    }

    private void assertHoverEvent(BaseComponent left, BaseComponent right) {
        HoverEvent leftHoverEvent = left.getHoverEvent();
        HoverEvent rightHoverEvent = right.getHoverEvent();
        if (rightHoverEvent == null) {
            if (leftHoverEvent != null) {
                throw new AssertionError("Expected no hover event but got \"" + leftHoverEvent + "\"");
            }
        } else {
            if (leftHoverEvent == null) {
                throw new AssertionError("Expected hover event \"" + rightHoverEvent + "\" but got none");
            }
            HoverEvent.Action leftAction = leftHoverEvent.getAction();
            HoverEvent.Action rightAction = rightHoverEvent.getAction();
            if (!leftAction.equals(rightAction)) {
                throw new AssertionError("Expected hover action \"" + rightAction + "\" but got \"" + leftAction + "\"");
            }
            List<Content> leftContents = leftHoverEvent.getContents();
            List<Content> rightContents = rightHoverEvent.getContents();
            for (int i = 0; i < leftContents.size(); i++) {
                String leftContent = leftContents.get(i).toString();
                String rightContent = rightContents.get(i).toString();
                if (!leftContent.equals(rightContent)) {
                    throw new AssertionError("Expected hover content \"" + rightContent + "\" but got \"" + leftContent + "\"");
                }
            }
            boolean leftLegacy = leftHoverEvent.isLegacy();
            boolean rightLegacy = rightHoverEvent.isLegacy();
            if (leftLegacy != rightLegacy) {
                throw new AssertionError("Expected hover legacy \"" + rightLegacy + "\" but got \"" + leftLegacy + "\"");
            }
        }
    }

    private void assertInsertion(BaseComponent left, BaseComponent right) {
        String leftInsertion = left.getInsertion();
        String rightInsertion = right.getInsertion();
        if (rightInsertion == null) {
            if (leftInsertion != null) {
                throw new AssertionError("Expected no insertion but got \"" + leftInsertion + "\"");
            }
        } else {
            if (leftInsertion == null) {
                throw new AssertionError("Expected insertion \"" + rightInsertion + "\" but got none");
            }
            if (!leftInsertion.equals(rightInsertion)) {
                throw new AssertionError("Expected insertion \"" + rightInsertion + "\" but got \"" + leftInsertion + "\"");
            }
        }
    }

    @Override
    public String toString() {
        return right.toLegacyText();
    }

    public Class<ComponentMatcher> type() {
        return ComponentMatcher.class;
    }
}
