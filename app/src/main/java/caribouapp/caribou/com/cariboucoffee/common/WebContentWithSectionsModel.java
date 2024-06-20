package caribouapp.caribou.com.cariboucoffee.common;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsWebContentSection;

/**
 * Created by andressegurola on 11/14/17.
 */

public class WebContentWithSectionsModel {

    private List<WebContentSection> mWebContentSections;

    public WebContentWithSectionsModel(List<CmsWebContentSection> cmsWebContentSectionList) {
        createWebContentSections(cmsWebContentSectionList);
    }

    private void createWebContentSections(List<CmsWebContentSection> items) {
        mWebContentSections = new ArrayList<>();
        for (CmsWebContentSection cmsTerms : items) {
            WebContentSection webContentSection = new WebContentSection();
            webContentSection.setTitle(cmsTerms.getTitle());
            webContentSection.setHtml(cmsTerms.getBodyRendered());
            mWebContentSections.add(mWebContentSections.size(), webContentSection);
        }
    }

    public List<WebContentSection> getWebContentSections() {
        return mWebContentSections;
    }

    public void setWebContentSections(List<WebContentSection> webContentSections) {
        mWebContentSections = webContentSections;
    }
}
