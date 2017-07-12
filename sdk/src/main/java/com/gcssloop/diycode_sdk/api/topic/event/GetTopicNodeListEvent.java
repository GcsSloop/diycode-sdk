package com.gcssloop.diycode_sdk.api.topic.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gcssloop.diycode_sdk.api.base.event.BaseEvent;
import com.gcssloop.diycode_sdk.api.topic.bean.Node;

import java.util.List;

/**
 * Created by Zack on 2017/7/12.
 */

public class GetTopicNodeListEvent extends BaseEvent<List<Node>> {

    public GetTopicNodeListEvent(@Nullable String uuid) {
        super(uuid);
    }

    public GetTopicNodeListEvent(@Nullable String uuid, @NonNull Integer code, @Nullable List<Node> nodes) {
        super(uuid, code, nodes);
    }
}
