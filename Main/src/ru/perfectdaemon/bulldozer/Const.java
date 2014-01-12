package ru.perfectdaemon.bulldozer;

/**
 * Created by daemon on 07.01.14.
 */
public class Const
{
    public static float EPSILON = 0.0001f;

    //Texture names
    public static final String FILE_ATLAS  = "atlas.atlas";

    public static final String BTN_NORMAL  = "btn_normal.png";
    public static final String BTN_OVER    = "btn_over.png";
    public static final String BTN_CLICK   = "btn_click.png";
    public static final String SLIDER_BTN  = "slider_btn.png";
    public static final String SLIDER_BACK  = "slider_back.png";
    public static final String SLIDER_OVER  = "slider_over.png";

    public static final String DOZER_BODY  = "block.png";
    public static final String DOZER_WHEEL = "circle.png";

    public static final int GAME_WIDTH = 25;
    public static final int GAME_HEIGHT = 15;

    //Physics
    public static final float PHYSIC_STEP = 1 / 60f;
    public static final int PHYSIC_VEL_IT = 8;
    public static final int PHYSIC_POS_IT = 4;

    public static final short CAT_PLAYER  = 0x0001;
    public static final short CAT_WHEELS  = 0x0002;
    public static final short CAT_ENEMY   = 0x0004;
    public static final short CAT_STATIC  = 0x0008;
    public static final short CAT_BONUS   = 0x0010;
    public static final short CAT_SENSOR  = 0x0020;
    public static final short CAT_DYNAMIC = 0x0040;

    public static final short MASK_PLAYER = CAT_ENEMY | CAT_STATIC | CAT_BONUS | CAT_SENSOR | CAT_DYNAMIC;
    public static final short MASK_PLAYER_WHEELS = CAT_ENEMY | CAT_STATIC | CAT_BONUS | CAT_DYNAMIC;
    public static final short MASK_ENEMY  = CAT_PLAYER | CAT_WHEELS | CAT_STATIC | CAT_DYNAMIC;
    public static final short MASK_SENSOR = CAT_DYNAMIC;
    public static final short MASK_DYNAMIC = CAT_PLAYER | CAT_WHEELS | CAT_ENEMY | CAT_STATIC | CAT_DYNAMIC | CAT_SENSOR;
    public static final short MASK_EARTH = CAT_PLAYER | CAT_WHEELS | CAT_ENEMY | CAT_DYNAMIC | CAT_SENSOR;

    public static final short GROUP_PLAYER = -1;

    //player
    public static final float PLAYER_CHANGE_GEAR_THRESHOLD = 2.0f;
    public static final float PLAYER_MIN_MOTOR_SPEED = 10.0f;

    public static final float CAM_CHANGEDIR_THRESHOLD = 1.5f;
    public static final float CAM_CHANGEDIR_TIMEOUT = 1.0f;
    public static final float CAM_SMOOTH = 3.0f;

    //Level
    public static final float LEVEL_LIMIT_MIN_X = 0.0f;
    public static final float LEVEL_LIMIT_MIN_Y = -2.5f;

    public static final float LEVEL_LIMIT_MAX_X = -7.5f;
    public static final float LEVEL_LIMIT_MAX_Y = 1.5f;
}
