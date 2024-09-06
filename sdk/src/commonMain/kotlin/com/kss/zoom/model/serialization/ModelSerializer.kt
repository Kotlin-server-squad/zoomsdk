package com.kss.zoom.model.serialization

import com.kss.zoom.model.context.DynamicContext
import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.Model
import com.kss.zoom.model.context.context
import com.kss.zoom.model.api.ModelSerializer as ApiModelSerializer
import com.kss.zoom.model.api.Model as ApiModel

abstract class ModelSerializer<A : ApiModel<*>, T : Model>(
    vararg property: DynamicProperty<*>,
) {
    protected val context: DynamicContext = context(*property)

    protected fun toApiModel(json: String): A = serializer.toModel(json)

    protected abstract val serializer: ApiModelSerializer<A>

    abstract fun toModel(json: String): T
}
