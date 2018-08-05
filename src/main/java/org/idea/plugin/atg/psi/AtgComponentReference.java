package org.idea.plugin.atg.psi;

import com.intellij.lang.properties.psi.impl.PropertiesFileImpl;
import com.intellij.lang.properties.psi.impl.PropertyValueImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.idea.plugin.atg.util.AtgComponentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class AtgComponentReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String componentName;

    public AtgComponentReference(@NotNull PropertyValueImpl element, TextRange textRange) {
        super(element, textRange);
        componentName = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public AtgComponentReference(@NotNull String componentName, @NotNull TextRange textRange, @NotNull PsiPlainTextFile originalFile) {
        super(originalFile, textRange);
        this.componentName = componentName;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        Collection<PropertiesFileImpl> applicableComponents = AtgComponentUtil.getApplicableComponentsByName(componentName, project);
        return applicableComponents.stream()
                .map(element -> new PsiElementResolveResult(element, true))
                .toArray(ResolveResult[]::new);
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        return new String[0];
    }

    @Override
    public PsiElement handleElementRename(final String newElementName) {
        int endIndex = componentName.contains("/") ? componentName.lastIndexOf("/") + 1 : 0;
        String newComponentName = componentName.substring(0, endIndex) + newElementName.replace(".properties", "");
        return super.handleElementRename(newComponentName);
    }

    @Override
    @SuppressWarnings("OptionalIsPresent")
    public PsiElement bindToElement(@NotNull PsiElement element) {
        if (element instanceof PropertiesFileImpl) {
            Optional<String> newComponentName = AtgComponentUtil.getComponentCanonicalName((PropertiesFileImpl) element);
            if (newComponentName.isPresent()) {
                return super.handleElementRename(newComponentName.get());
            }
            return null;
        }
        return super.bindToElement(element);
    }
}
