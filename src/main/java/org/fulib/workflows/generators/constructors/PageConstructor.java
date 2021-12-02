package org.fulib.workflows.generators.constructors;

import org.fulib.workflows.events.Page;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

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

        for (int i = 0; i <= currentPage.getContent().size(); i++) {
            if (currentPage.getContent().get(i) == null) {
                continue;
            }

            String key = currentPage.getContent().get(i).a;
            String value = currentPage.getContent().get(i).b;

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
