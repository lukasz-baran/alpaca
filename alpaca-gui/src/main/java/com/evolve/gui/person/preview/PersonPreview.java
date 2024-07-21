package com.evolve.gui.person.preview;

import com.evolve.domain.Person;
import javafx.scene.control.TreeItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record PersonPreview(boolean expand, Person person, TreeItem<PersonTreeItem> root,
                            Map<String, List<Object>> byTags) {

    public void compareWith(PersonPreview otherPersonPreview) {
        compareWith(new ArrayList<>(), root, otherPersonPreview);
    }

    private void compareWith(List<String> breadcrumb, TreeItem<PersonTreeItem> node,
                             PersonPreview otherPersonPreview) {
        final PersonTreeItem treeItem = node.getValue();
        final List<String> newBreadcrumb = new ArrayList<>(breadcrumb);
        newBreadcrumb.add(treeItem.getTag());

        final String key = String.join(".", newBreadcrumb);

        List<Object> otherElements = otherPersonPreview.byTags().getOrDefault(key, List.of());

        if (otherElements.isEmpty()) {
            treeItem.setDifference(PersonTreeItemDifference.CHANGED);
        } else if (otherElements.size() == 1) {
            final Object otherElement = otherElements.get(0);

            if (otherElement instanceof String otherString) {

                if (StringUtils.equals(treeItem.getDisplayText(), otherString)) {
                    treeItem.setDifference(PersonTreeItemDifference.SAME);
                } else {
                    treeItem.setDifference(PersonTreeItemDifference.CHANGED);
                }

            } else {
                treeItem.getValue().ifPresentOrElse(
                        currentObject -> treeItem.setDifference(currentObject.equals(otherElement) ?
                                PersonTreeItemDifference.SAME : PersonTreeItemDifference.CHANGED),
                        () -> treeItem.setDifference(PersonTreeItemDifference.CHANGED)
                );
            }
        }

        if (newBreadcrumb.size() == 3) {
            // copy difference from parent:
            for (TreeItem<PersonTreeItem> child : node.getChildren()) {
                child.getValue().setDifference(treeItem.getDifference());
            }
        } else {
            for (TreeItem<PersonTreeItem> child : node.getChildren()) {
                compareWith(newBreadcrumb, child, otherPersonPreview);
            }
        }

    }
}
