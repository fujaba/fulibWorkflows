package org.fulib.workflows.generators.constructors;

import org.fulib.workflows.events.Page;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.List;
import java.util.Objects;

public class PageConstructor {
    private Page currentPage;

    private STGroupFile pageGroup;

    public String buildPage(Page page) {
        currentPage = page;

        pageGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("../Page.stg")));
        StringBuilder pageBody = new StringBuilder();

        // Complete the page
        ST st = pageGroup.getInstanceOf("page");
        st.add("content", buildPageContent());
        st.add("pageName", currentPage.getName());

        pageBody.append(st.render());
        return pageBody.toString();
    }

    private String buildPageContent() {
        ST st;
        StringBuilder contentBody = new StringBuilder();

        List<String> list = currentPage.getContent().keySet().stream().toList();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            String value = currentPage.getContent().get(key);

            if (key.contains("text")) {
                st = pageGroup.getInstanceOf("text");
                st.add("text", value);
                contentBody.append(st.render());
            } else if (key.contains("input")) {
                st = pageGroup.getInstanceOf("input");
                st.add("id", i + "input");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("password")) {
                st = pageGroup.getInstanceOf("password");
                st.add("id", i + "password");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("button")) {
                st = pageGroup.getInstanceOf("button");
                st.add("description", value);
                contentBody.append(st.render());
            }
        }

        return contentBody.toString();
    }
}
