package org.fulib.workflows.generators.constructors;

import org.fulib.workflows.events.Page;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;
import java.util.Objects;

public class FxmlConstructor {
    private Page currentPage;

    private STGroupFile fxmlGroup;

    public String buildFxml(Page page) {
        currentPage = page;
        URL resource = PageConstructor.class.getResource("Fxml.stg");

        fxmlGroup = new STGroupFile(Objects.requireNonNull(resource));
        StringBuilder pageBody = new StringBuilder();

        // Complete the page
        ST st = fxmlGroup.getInstanceOf("view");
        st.add("content", buildFxmlContent());
        st.add("viewName", currentPage.getName());

        pageBody.append(st.render());
        return pageBody.toString();
    }

    private String buildFxmlContent() {
        ST st;
        StringBuilder contentBody = new StringBuilder();

        for (int i = 0; i <= currentPage.getContent().size(); i++) {
            if (currentPage.getContent().get(i) == null) {
                continue;
            }

            String key = currentPage.getContent().get(i).a;
            String value = currentPage.getContent().get(i).b;

            if (key.contains("text")) {
                st = fxmlGroup.getInstanceOf("text");
                st.add("id", i + "text");
                st.add("text", value);
                contentBody.append(st.render());
            } else if (key.contains("input")) {
                st = fxmlGroup.getInstanceOf("input");
                st.add("id", i + "input");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("password")) {
                st = fxmlGroup.getInstanceOf("password");
                st.add("id", i + "password");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("button")) {
                st = fxmlGroup.getInstanceOf("button");
                st.add("id", i + "button");
                st.add("description", value);
                contentBody.append(st.render());
            }
        }

        return contentBody.toString();
    }
}
