/**
 * Created by daemon on 05.01.14.
 */

package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.io.File;
import java.nio.charset.Charset;


public class CheetahTextureAtlas implements Disposable
{
    public class TextureNotFoundException extends RuntimeException
    {
        public TextureNotFoundException(String s)
        {
            super(s);
        }
    }

    private class TextureInfo
    {
        public String name;
        public int w, h, x, y;
        public boolean rotated;
        public TextureRegion region;

        public TextureInfo(String name, int x, int y, int w, int h, boolean rotated)
        {
            this.name = name;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.rotated = rotated;
        }

        public void createTextureRegion()
        {
            this.region = new TextureRegion(atlas, x, y, w, h);
        }
    }

    private TextureInfo[] textureInfos;

    private Texture atlas;
    private String atlasName;

    public CheetahTextureAtlas(FileHandle atlasFile)
    {
        atlasName = atlasFile.name();
        String fileContent = new String(atlasFile.readBytes(), Charset.defaultCharset());
        String[] lines = fileContent.split("(\\r\\n)|(\\n\\r)");
        String textureFileName = atlasFile.path();
        textureFileName = textureFileName.substring(0, textureFileName.lastIndexOf('/') + 1);
        textureFileName += lines[0].split(":")[1].trim();
        atlas = new Texture(Gdx.files.internal(textureFileName));
        textureInfos = new TextureInfo[lines.length - 1];
        for (int i = 1; i < lines.length; i++)
        {
            String[] params = lines[i].split("\\t");
            textureInfos[i - 1] = new TextureInfo(params[0], //Name
                    Integer.parseInt(params[1]), Integer.parseInt(params[2]), //X, Y
                    Integer.parseInt(params[3]), Integer.parseInt(params[4]), //W, H
                    ((params.length > 9) && (params[9] == "r")));             //Rotated
            textureInfos[i - 1].createTextureRegion();
        }
    }

    public TextureRegion getTextureRegion(String textureName)
    {
        for(int i = 0; i < textureInfos.length; i++)
        {
            if (textureInfos[i].name.equals(textureName))
            {
                return textureInfos[i].region;
            }
        }
        Gdx.app.log("Game", String.format("Texture `%s` was not found in atlas `%s`", textureName, atlasName));
        return new TextureRegion(atlas);
    }


    @Override
    public void dispose()
    {
        atlas.dispose();
    }
}
