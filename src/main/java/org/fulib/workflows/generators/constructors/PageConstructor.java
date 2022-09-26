package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.Page;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The PageConstructor builds a html mockup from a page event from an fulibWorkflows Board.
 */
public class PageConstructor {
    private Page currentPage;
    private Map<String, Page> divPageMap;
    private STGroupFile pageGroup;

    private boolean webGeneration;

    /**
     * Uses string templates to build html file containing a mockup
     *
     * @param page       Page object filled with the content of the mockup
     * @param divPageMap map of known divs
     * @return fxml file content as String
     */
    public String buildPage(Page page, List<Integer> targetPageIndexList, Map<String, Page> divPageMap, boolean webGeneration) {
        currentPage = page;
        this.divPageMap = divPageMap;
        this.webGeneration = webGeneration;

        initPageGroup();

        StringBuilder pageBody = new StringBuilder();

        // Complete the page
        ST st = pageGroup.getInstanceOf("page");
        String pageContent = buildPageContent(targetPageIndexList);
        if (pageContent == null) {
            return null;
        }
        st.add("content", pageContent);
        st.add("pageName", currentPage.getName());

        pageBody.append(st.render());
        return pageBody.toString();
    }

    private void initPageGroup() {
        if (pageGroup == null) {
            URL resource = PageConstructor.class.getResource("Page.stg");

            pageGroup = new STGroupFile(Objects.requireNonNull(resource));
        }
    }

    public String buildDivRow(Page page, List<Integer> targetPageIndexList, Map<String, Page> divPageMap) {
        currentPage = page;
        this.divPageMap = divPageMap;
        initPageGroup();
        // Complete the page
        String pageContent = buildPageContent(targetPageIndexList);
        return pageContent;
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

            if (key.equals("value") || key.equals("targetPage")) {
                continue;
            }

            String value = currentPage.getContent().get(i).b;

            Pair<String, String> nextElement = new Pair<>("", "");

            if (i + 1 < currentPage.getContent().size()) {
                nextElement = currentPage.getContent().get(i + 1);
            }

            if (key.contains("text")) {
                buildText(contentBody, value);
            } else if (key.contains("input")) {
                buildInput(contentBody, i, value, nextElement);
            } else if (key.contains("password")) {
                buildPassword(contentBody, i, value, nextElement);
            } else if (key.contains("button")) {
                targetIndex = buildButton(targetPageIndexList, contentBody, targetIndex, value);
            } else if (key.equals("div")) {
                boolean complete = buildDivItems(targetPageIndexList, contentBody, value);
                if (!complete) {
                    return null;
                }
            }
        }

        return contentBody.toString();
    }

    private boolean buildDivItems(List<Integer> targetPageIndexList, StringBuilder contentBody, String value) {
        ST st;
        // first add the list of div names
        st = pageGroup.getInstanceOf("rowOfDiv");

        String[] split = stripBraces(value).split(",");
        if (divPageMap != null) {
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
                Page divPage = divPageMap.get(split[i]);
                if (divPage != null) {
                    String content = new PageConstructor().buildDivRow(divPage, targetPageIndexList, this.divPageMap);
                    if (content == null) {
                        return false;
                    }
                    split[i] = content;
                } else {
                    return false;
                }
            }
        }
        st.add("divList", split);
        String str = st.render();
        contentBody.append(str);

        return true;
    }

    private int buildButton(List<Integer> targetPageIndexList, StringBuilder contentBody, int targetIndex, String value) {
        ST st;
        if (this.webGeneration) {
            st = pageGroup.getInstanceOf("button");
        } else {
            st = pageGroup.getInstanceOf("buttonAlone");
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
        return targetIndex;
    }

    private void buildPassword(StringBuilder contentBody, int i, String value, Pair<String, String> nextElement) {
        ST st;
        st = pageGroup.getInstanceOf("password");
        st.add("id", i + "password");
        st.add("label", value);
        st.add("value", getValue(nextElement));
        contentBody.append(st.render());
    }

    private void buildInput(StringBuilder contentBody, int i, String value, Pair<String, String> nextElement) {
        ST st;
        st = pageGroup.getInstanceOf("input");
        st.add("id", i + "input");
        st.add("label", value);
        st.add("value", getValue(nextElement));
        contentBody.append(st.render());
    }

    private void buildText(StringBuilder contentBody, String value) {
        ST st;
        st = pageGroup.getInstanceOf("text");
        if (value != null && value.startsWith("<pre>")) {
            st = pageGroup.getInstanceOf("pre");
        }
        st.add("text", value);
        String content = st.render();
        contentBody.append(content);
    }

    private String stripBraces(String value) {
        int pos = value.indexOf('[');
        if (pos >= 0) {
            value = value.substring(pos + 1);
        }
        pos = value.indexOf(']');
        if (pos >= 0) {
            value = value.substring(0, pos);
        }
        return value;
    }

    private String getValue(Pair<String, String> nextElement) {
        String result = "";

        if (nextElement.a.equals("value")) {
            result = nextElement.b;
        }

        return result;
    }
}
