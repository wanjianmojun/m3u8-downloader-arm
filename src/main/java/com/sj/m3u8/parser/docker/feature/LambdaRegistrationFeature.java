package com.sj.m3u8.parser.docker.feature;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;
import com.sj.m3u8.parser.docker.entity.McdConfig;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdSubscribe;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.service.impl.McdConfigServiceImpl;
import com.sj.m3u8.parser.docker.service.impl.McdParseServiceImpl;
import com.sj.m3u8.parser.docker.service.impl.McdSubscribeServiceImpl;
import com.sj.m3u8.parser.docker.service.impl.McdTaskServiceImpl;
import com.sj.m3u8.parser.docker.service.impl.TaskServiceImpl;
import com.sj.m3u8.parser.docker.task.TaskExecutor;

public class LambdaRegistrationFeature implements Feature {

	@Override
	public void duringSetup(DuringSetupAccess access) {
		RuntimeSerialization.registerLambdaCapturingClass(McdConfig.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdParse.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdSubscribe.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdTask.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdConfigServiceImpl.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdParseServiceImpl.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdSubscribeServiceImpl.class);
		RuntimeSerialization.registerLambdaCapturingClass(McdTaskServiceImpl.class);
		RuntimeSerialization.registerLambdaCapturingClass(TaskServiceImpl.class);
		RuntimeSerialization.registerLambdaCapturingClass(TaskExecutor.class);
	}

}