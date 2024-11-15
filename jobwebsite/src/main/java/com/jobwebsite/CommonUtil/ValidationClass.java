package com.jobwebsite.CommonUtil;



import java.util.regex.Pattern;

public class ValidationClass {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.@\\- '()]*$");
    public static final Pattern ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.'-]+$");
    public static final Pattern CUISINE_PATTERN = Pattern.compile("^[a-zA-Z0-9- ]+$");
    public static final Pattern RATING_PATTERN = Pattern.compile("^[0-5](\\.[0-9])?$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    public static final Pattern RESTAURANT_CONTANCT_PATTERN = Pattern.compile("^(?:\\d{10}|(?:\\d{3,5}-\\d{6,8}))$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
    public static final Pattern GENDER_PATTERN = Pattern.compile("^(Male|Female|Other)$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!])([a-zA-Z\\d@#$%^&+=!]{6,20})$");
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z]+$|^\\d{10}$");
    public static final Pattern PINCODE_PATTERN = Pattern.compile("^[0-9]{6}$");
    public static final Pattern MENU_ITEM_PATTERN = Pattern.compile("^[a-zA-Z0-9_.@\\- '()]*$");
    public static final Pattern QUANTITY_PATTERN = Pattern.compile("^[0-9]+$");
    public static final Pattern PRICE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    public static final Pattern ORDER_TYPE_PATTERN = Pattern.compile("^(Dine-in|Delivery)$");
    public static final Pattern DELIVERY_CHARGE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    public static final Pattern DESCRIPTION_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.:'\"\\-()!]*$");
    public static final Pattern CATEGORY_PATTERN = Pattern.compile("VEG|NON_VEG");
    public static final Pattern IMAGE_PATTERN = Pattern.compile("([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp|webp))$)");

}

