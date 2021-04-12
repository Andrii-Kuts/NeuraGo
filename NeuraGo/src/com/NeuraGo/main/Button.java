package com.NeuraGo.main;

public interface Button
{
    public void OnMouseHover(float x, float y);
    public void OnMouseLeave();
    public void OnClick(float x, float y);
    public void OnRelease(float x, float y);
    public boolean Intersects(float x, float y);
}
