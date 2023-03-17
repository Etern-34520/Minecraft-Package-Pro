package io;

import com.google.gson.*;

public class JsonUtils
{

    /**
     * Gets the string value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static String getString(JsonElement p_151206_0_, String p_151206_1_)
    {
        if (p_151206_0_.isJsonPrimitive())
        {
            return p_151206_0_.getAsString();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + toString(p_151206_0_));
        }
    }

    /**
     * Gets the string value of the field on the JsonObject with the given name.
     */
    public static String getString(JsonObject p_151200_0_, String p_151200_1_)
    {
        if (p_151200_0_.has(p_151200_1_))
        {
            return getString(p_151200_0_.get(p_151200_1_), p_151200_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
        }
    }

    /**
     * Gets the given JsonElement as a JsonObject.  Expects the second parameter to be the name of the element's field
     * if an error message needs to be thrown.
     */
    public static JsonObject getJsonObject(JsonElement p_151210_0_, String p_151210_1_)
    {
        if (p_151210_0_.isJsonObject())
        {
            return p_151210_0_.getAsJsonObject();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + toString(p_151210_0_));
        }
    }

    /**
     * Gets the JsonObject field on the JsonObject with the given name, or the given default value if the field is
     * missing.
     */
    public static JsonObject getJsonObject(JsonObject p_151218_0_, String p_151218_1_, JsonObject p_151218_2_)
    {
        return p_151218_0_.has(p_151218_1_) ? getJsonObject(p_151218_0_.get(p_151218_1_), p_151218_1_) : p_151218_2_;
    }

    /**
     * Gets a human-readable description of the given JsonElement's type.  For example: "a number (4)"
     */
    public static String toString(JsonElement p_151222_0_)
    {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(p_151222_0_), "...", 10);

        if (p_151222_0_ == null)
        {
            return "null (missing)";
        }
        else if (p_151222_0_.isJsonNull())
        {
            return "null (json)";
        }
        else if (p_151222_0_.isJsonArray())
        {
            return "an array (" + s + ")";
        }
        else if (p_151222_0_.isJsonObject())
        {
            return "an object (" + s + ")";
        }
        else
        {
            if (p_151222_0_.isJsonPrimitive())
            {
                JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();

                if (jsonprimitive.isNumber())
                {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean())
                {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }
}
