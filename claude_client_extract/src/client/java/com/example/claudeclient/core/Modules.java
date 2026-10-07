package com.example.claudeclient.core;

import com.example.claudeclient.modules.*;

import java.util.List;

public class Modules {
    public static final List<Module> ALL = List.of(
        new LagCut(), new AnchorHud(), new TotemHud(), new AutoSell());
}
