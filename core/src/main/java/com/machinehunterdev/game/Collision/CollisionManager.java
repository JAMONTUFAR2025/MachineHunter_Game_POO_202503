package com.machinehunterdev.game.Collision;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class CollisionManager {

    public static boolean checkPixelPerfectCollision(Sprite spriteA, Sprite spriteB) {
        if (spriteA == null || spriteB == null) {
            return false;
        }

        Rectangle boundsA = spriteA.getBoundingRectangle();
        Rectangle boundsB = spriteB.getBoundingRectangle();

        if (!boundsA.overlaps(boundsB)) {
            return false;
        }

        Rectangle intersection = new Rectangle();
        Intersector.intersectRectangles(boundsA, boundsB, intersection);

        if (intersection.width < 1 || intersection.height < 1) {
            return false;
        }

        TextureData textureDataA = spriteA.getTexture().getTextureData();
        if (!textureDataA.isPrepared()) {
            textureDataA.prepare();
        }
        Pixmap pixmapA = textureDataA.consumePixmap();

        TextureData textureDataB = spriteB.getTexture().getTextureData();
        if (!textureDataB.isPrepared()) {
            textureDataB.prepare();
        }
        Pixmap pixmapB = textureDataB.consumePixmap();

        for (int x = 0; x < (int)intersection.width; x++) {
            for (int y = 0; y < (int)intersection.height; y++) {
                // Transform intersection coordinates to local sprite coordinates
                float relXA = (intersection.x - boundsA.x) + x;
                float relYA = (intersection.y - boundsA.y) + y;

                float relXB = (intersection.x - boundsB.x) + x;
                float relYB = (intersection.y - boundsB.y) + y;

                // Transform to texture region coordinates, accounting for flipping
                int texXA = spriteA.getRegionX() + (spriteA.isFlipX() ? (spriteA.getRegionWidth() - 1 - (int)relXA) : (int)relXA);
                int texYA = spriteA.getRegionY() + (spriteA.isFlipY() ? (int)relYA : (spriteA.getRegionHeight() - 1 - (int)relYA));

                int texXB = spriteB.getRegionX() + (spriteB.isFlipX() ? (spriteB.getRegionWidth() - 1 - (int)relXB) : (int)relXB);
                int texYB = spriteB.getRegionY() + (spriteB.isFlipY() ? (int)relYB : (spriteB.getRegionHeight() - 1 - (int)relYB));

                // Get pixel color
                int pixelA = pixmapA.getPixel(texXA, texYA);
                int pixelB = pixmapB.getPixel(texXB, texYB);

                // Check alpha channel (last 8 bits)
                if ((pixelA & 0x000000ff) != 0 && (pixelB & 0x000000ff) != 0) {
                    // Collision detected
                    // Pixmaps are not disposed because they are consumed and managed by GC
                    return true;
                }
            }
        }

        return false;
    }
}
