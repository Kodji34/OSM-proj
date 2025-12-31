package com.example.school.dto;

import com.example.school.entity.ModuleDefinition;
import com.example.school.entity.ModuleStatus;

public class ModuleItem {
    private final ModuleDefinition module;
    private final ModuleStatus status;
    private final boolean isNew;

    public ModuleItem(ModuleDefinition module, ModuleStatus status) {
        this.module = module;
        this.status = status;
        this.isNew = module != null && module.isRecentlyCreated();
    }

    public ModuleDefinition getModule() { return module; }
    public ModuleStatus getStatus() { return status; }
    public boolean isNew() { return isNew; }
}
