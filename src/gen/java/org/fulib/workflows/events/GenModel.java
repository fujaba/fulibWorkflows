package org.fulib.workflows.events;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;

import java.util.List;
import java.util.Map;

public class GenModel implements ClassModelDecorator {
    class Board {
        List<Workflow> workflows;
    }

    class BaseNote {
        String name;
        int index;
    }

    class Workflow extends BaseNote {
        List<BaseNote> notes;
    }

    class Event extends BaseNote {
        Map<String, String> data;
    }

    class Page extends BaseNote {
        Map<String, String> content;
    }


    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}

