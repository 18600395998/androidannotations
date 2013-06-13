/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.handler.rest;

import com.sun.codemodel.*;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.TreeMap;

public class PostHandler extends RestMethodHandler {

	public PostHandler(ProcessingEnvironment processingEnvironment) {
		super(Post.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.doesNotReturnPrimitive((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter((ExecutableElement) element, valid);
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Post annotation = element.getAnnotation(Post.class);
		return annotation.value();
	}

	@Override
	protected JExpression getRequestEntity(Element element, JBlock methodBody, TreeMap<String, JVar> methodParams) {
		String mediaType = restAnnotationHelper.acceptedHeaders((ExecutableElement) element);
		JVar httpRestHeaders = null;
		if (mediaType != null) {
			httpRestHeaders = restAnnotationHelper.declareAcceptedHttpHeaders(processHolder, methodBody, mediaType);
		}
		return restAnnotationHelper.declareHttpEntity(processHolder, methodBody, methodParams, httpRestHeaders);
	}

	protected JExpression getResponseClass(Element element, RestHolder holder) {
		return restAnnotationHelper.getResponseClass(element, holder);
	}

	protected JInvocation addResultCallMethod(JInvocation exchangeCall, JClass methodReturnClass) {
		if (methodReturnClass != null && !methodReturnClass.fullName().startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			return JExpr.invoke(exchangeCall, "getBody");
		}
		return exchangeCall;
	}
}
