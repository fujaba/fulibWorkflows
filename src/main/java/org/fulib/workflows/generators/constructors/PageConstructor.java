package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.Page;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * The PageConstructor builds a html mockup from a page event from an fulibWorkflows Board.
 */
public class PageConstructor {
    private Page currentPage;

    private STGroupFile pageGroup;

    private boolean standAlone;

    /**
     * Uses string templates to build html file containing a mockup
     *
     * @param page Page object filled with the content of the mockup
     * @return fxml file content as String
     */
    public String buildPage(Page page, List<Integer> targetPageIndexList) {
        currentPage = page;

        URL resource = PageConstructor.class.getResource("Page.stg");

        pageGroup = new STGroupFile(Objects.requireNonNull(resource));
        StringBuilder pageBody = new StringBuilder();

        // Complete the page
        ST st = pageGroup.getInstanceOf("page");
        st.add("content", buildPageContent(targetPageIndexList));
        st.add("pageName", currentPage.getName());

        pageBody.append(st.render());
        return pageBody.toString();
    }

    private String buildPageContent(List<Integer> targetPageIndexList) {
        ST st;
        StringBuilder contentBody = new StringBuilder();

        int targetIndex = 0;

        for (int i = 0; i <= currentPage.getContent().size(); i++) {
            if (currentPage.getContent().get(i) == null) {
                continue;
            }

            String key = currentPage.getContent().get(i).a;

            if (key.equals("fill") || key.equals("targetPage")) {
                continue;
            }

            String value = currentPage.getContent().get(i).b;

            Pair<String, String> nextElement = new Pair<>("", "");

            if (i + 1 < currentPage.getContent().size()) {
                nextElement = currentPage.getContent().get(i + 1);
            }

            if (key.contains("text")) {
                st = pageGroup.getInstanceOf("text");
                st.add("text", value);
                contentBody.append(st.render());
            } else if (key.contains("input")) {
                st = pageGroup.getInstanceOf("input");
                st.add("id", i + "input");
                st.add("label", value);
                st.add("fill", getFill(nextElement));
                contentBody.append(st.render());
            } else if (key.contains("password")) {
                st = pageGroup.getInstanceOf("password");
                st.add("id", i + "password");
                st.add("label", value);
                st.add("fill", getFill(nextElement));
                contentBody.append(st.render());
            } else if (key.contains("button")) {
                if (this.standAlone) {
                    st = pageGroup.getInstanceOf("buttonAlone");
                } else {
                    st = pageGroup.getInstanceOf("button");
                }
                st.add("description", value);

                int foundTargetIndex;

                if (targetIndex < targetPageIndexList.size()) {
                    foundTargetIndex = targetPageIndexList.get(targetIndex);
                } else {
                    foundTargetIndex = currentPage.getIndex();
                }

                st.add("targetIndex", foundTargetIndex);
                targetIndex++;
                contentBody.append(st.render());
            }
        }

        return contentBody.toString();
    }

    private String getFill(Pair<String, String> nextElement) {
        String result = "";

        if (nextElement.a.equals("fill")) {
            result = nextElement.b;
        }

        return result;
    }

    public PageConstructor setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
        return this;
    }
}
