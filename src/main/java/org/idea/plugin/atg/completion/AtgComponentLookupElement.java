package org.idea.plugin.atg.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.lang.properties.psi.impl.PropertiesFileImpl;
import org.idea.plugin.atg.util.AtgComponentUtil;
import org.jetbrains.annotations.NotNull;

public class AtgComponentLookupElement extends LookupElement {
    private final PropertiesFileImpl targetComponent;
    private final String presentableName;

    public AtgComponentLookupElement(@NotNull final PropertiesFileImpl propertiesFile) {
        this.targetComponent = propertiesFile;
        this.presentableName = AtgComponentUtil.getComponentCanonicalName(targetComponent).orElse(targetComponent.getVirtualFile().getNameWithoutExtension());
    }

    @NotNull
    @Override
    public Object getObject() {
        return targetComponent;
    }

    @Override
    @NotNull
    public String getLookupString() {
        return presentableName;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(presentableName);
        AtgComponentUtil.getComponentClassesStr(targetComponent).stream().findFirst().ifPresent(presentation::setTypeText);
        presentation.setTypeGrayed(true);
    }
}
