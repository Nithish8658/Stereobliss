/* */

package com.groza.Stereobliss.utils;

import com.groza.Stereobliss.models.GenericModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionCreator<T extends GenericModel> {

    public interface SectionChooser<T> {
        char getSectionName(T model);
    }

    private final SectionChooser<T> mSectionChooser;

    private final List<String> mSectionList;

    private final List<Integer> mSectionPositions;

    private final Map<Character, Integer> mPositionSectionMap;

    public SectionCreator(final SectionChooser<T> sectionChooser) {
        mSectionList = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        mPositionSectionMap = new HashMap<>();
        mSectionChooser = sectionChooser;
    }

    public void createSections(final List<T> modelData) {
        clearSections();

        final int count = modelData.size();

        if (count > 0) {
            T currentModel = modelData.get(0);

            char lastSection = mSectionChooser.getSectionName(currentModel);

            mSectionList.add(String.valueOf(lastSection));
            mSectionPositions.add(0);
            mPositionSectionMap.put(lastSection, mSectionList.size() - 1);

            for (int i = 1; i < count; i++) {
                currentModel = modelData.get(i);

                final char currentSection = mSectionChooser.getSectionName(currentModel);

                if (lastSection != currentSection) {
                    mSectionList.add("" + currentSection);

                    lastSection = currentSection;
                    mSectionPositions.add(i);
                    mPositionSectionMap.put(currentSection, mSectionList.size() - 1);
                }

            }
        }
    }

    public int getPositionForIndex(final int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    public int getSectionPositionForModel(final T model) {
        final char section = mSectionChooser.getSectionName(model);

        if (mPositionSectionMap.containsKey(section)) {
            return mPositionSectionMap.get(section);
        } else {
            return 0;
        }
    }

    public List<String> getSectionList() {
        return mSectionList;
    }

    public void clearSections() {
        mSectionList.clear();
        mSectionPositions.clear();
        mPositionSectionMap.clear();
    }
}
