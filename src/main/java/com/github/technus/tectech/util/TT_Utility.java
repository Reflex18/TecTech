package com.github.technus.tectech.util;

import java.lang.reflect.Field;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_TieredMachineBlock;

/**
 * Created by Tec on 21.03.2017.
 */
public final class TT_Utility {

    private TT_Utility() {}

    private static final StringBuilder STRING_BUILDER = new StringBuilder();
    private static final Map<Locale, Formatter> FORMATTER_MAP = new HashMap<>();

    private static Formatter getFormatter() {
        STRING_BUILDER.setLength(0);
        return FORMATTER_MAP.computeIfAbsent(
                Locale.getDefault(Locale.Category.FORMAT),
                locale -> new Formatter(STRING_BUILDER, locale));
    }

    public static String formatNumberExp(double value) {
        return getFormatter().format("%+.5E", value).toString();
    }

    // Formats to standard form.
    public static String toStandardForm(long number) {
        if (number == 0) {
            return "0";
        }

        int exponent = (int) Math.floor(Math.log10(Math.abs(number)));
        double mantissa = number / Math.pow(10, exponent);

        // Round the mantissa to two decimal places
        mantissa = Math.round(mantissa * 100.0) / 100.0;

        return mantissa + "*10^" + exponent;
    }

    public static int bitStringToInt(String bits) {
        if (bits == null) {
            return 0;
        }
        if (bits.length() > 32) {
            throw new NumberFormatException("Too long!");
        }
        return Integer.parseInt(bits, 2);
    }

    public static int hexStringToInt(String hex) {
        if (hex == null) {
            return 0;
        }
        if (hex.length() > 8) {
            throw new NumberFormatException("Too long!");
        }
        return Integer.parseInt(hex, 16);
    }

    public static double stringToDouble(String str) {
        if (str == null) {
            return 0;
        }
        return Double.parseDouble(str);
    }

    public static String longBitsToShortString(long number) {
        StringBuilder result = new StringBuilder(71);

        for (int i = 63; i >= 0; i--) {
            long mask = 1L << i;
            result.append((number & mask) != 0 ? ":" : ".");

            if (i % 8 == 0) {
                result.append('|');
            }
        }
        result.replace(result.length() - 1, result.length(), "");

        return result.toString();
    }

    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static String getUniqueIdentifier(ItemStack is) {
        return GameRegistry.findUniqueIdentifierFor(is.getItem()).modId + ':' + is.getUnlocalizedName();
    }

    public static byte getTier(long l) {
        byte b = -1;

        do {
            ++b;
            if (b >= CommonValues.V.length) {
                return b;
            }
        } while (l > CommonValues.V[b]);

        return b;
    }

    public static void setTier(int tier, Object me) {
        try {
            Field field = GT_MetaTileEntity_TieredMachineBlock.class.getField("mTier");
            field.setAccessible(true);
            field.set(me, (byte) tier);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Deprecated
    public static double receiveDouble(double previousValue, int startIndex, int index, int value) {
        return Double.longBitsToDouble(receiveLong(Double.doubleToLongBits(previousValue), startIndex, index, value));
    }

    public static long receiveLong(long previousValue, int startIndex, int index, int value) {
        value &= 0xFFFF;
        switch (index - startIndex) {
            case 0 -> {
                previousValue &= 0xFFFF_FFFF_FFFF_0000L;
                previousValue |= value;
            }
            case 1 -> {
                previousValue &= 0xFFFF_FFFF_0000_FFFFL;
                previousValue |= (long) value << 16;
            }
            case 2 -> {
                previousValue &= 0xFFFF_0000_FFFF_FFFFL;
                previousValue |= (long) value << 32;
            }
            case 3 -> {
                previousValue &= 0x0000_FFFF_FFFF_FFFFL;
                previousValue |= (long) value << 48;
            }
        }
        return previousValue;
    }

    @Deprecated
    public static float receiveFloat(float previousValue, int startIndex, int index, int value) {
        return Float.intBitsToFloat(receiveInteger(Float.floatToIntBits(previousValue), startIndex, index, value));
    }

    public static int receiveInteger(int previousValue, int startIndex, int index, int value) {
        value &= 0xFFFF;
        switch (index - startIndex) {
            case 0 -> {
                previousValue &= 0xFFFF_0000;
                previousValue |= value;
            }
            case 1 -> {
                previousValue &= 0x0000_FFFF;
                previousValue |= value << 16;
            }
        }
        return previousValue;
    }

    public static String doubleToString(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

}
