package ru.minyurovevgeniy.squads;

import android.graphics.Bitmap;

public class RandomPhoto
{
    String description="";
    Bitmap photo;

    RandomPhoto(String _description, Bitmap _photo)
    {
        this.description=_description;
        this.photo=_photo;
    }
}
