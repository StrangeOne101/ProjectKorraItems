package com.projectkorra.items;

import com.projectkorra.items.attribute.AttributeEvent;
import com.projectkorra.items.attribute.AttributeModification;

import java.util.Map;
import java.util.Set;

public interface ItemProperties {

    Map<AttributeEvent, Set<AttributeModification>> getPKIAttributes();
}
