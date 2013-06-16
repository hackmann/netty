/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */package io.netty.handler.timeout;

import io.netty.channel.ChannelHandlerContext;

public class IdleStateActivityTracker implements ActivityTracker {
    volatile long lastReadTime;
    boolean firstReaderIdleEvent = true;

    volatile long lastWriteTime;
    boolean firstWriterIdleEvent = true;

    boolean firstAllIdleEvent = true;

    @Override
    public void initialized(ChannelHandlerContext ctx) {
    }

    public void readActivity() {
        lastReadTime = System.currentTimeMillis();
        firstReaderIdleEvent = firstAllIdleEvent = true;
    }

    public void writeActivity() {
        lastWriteTime = System.currentTimeMillis();
        firstWriterIdleEvent = firstAllIdleEvent = true;
    }

    @Override
    public long getLastActivity() {
        return Math.max(lastReadTime, lastWriteTime);
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void timedOut(ChannelHandlerContext ctx) throws Exception {
        IdleStateEvent event;
        if (firstAllIdleEvent) {
            firstAllIdleEvent = false;
            event = IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT;
        } else {
            event = IdleStateEvent.ALL_IDLE_STATE_EVENT;
        }
        channelIdle(ctx, event);
    }
}
