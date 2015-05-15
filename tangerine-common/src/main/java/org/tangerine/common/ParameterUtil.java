package org.tangerine.common;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterUtil {

	public static Class<?> getTypeClass(Type parameterType) {
		Class<?> typeClz = null;
		if (parameterType instanceof Class) {
			typeClz = (Class<?>)parameterType;
		} else if (parameterType instanceof ParameterizedType) {
			typeClz = (Class<?>)((ParameterizedType)parameterType).getRawType();
		} else if (parameterType instanceof GenericArrayType) {
			typeClz = (Class<?>)((GenericArrayType)parameterType).getGenericComponentType();
		} else {
			throw new IllegalArgumentException("Unsupport parameter type[" + parameterType + "].");
		}
		return typeClz;
	}
	
	public static Class getParameterizedType(Type type) {
		return ((Class) ((ParameterizedType)type).getActualTypeArguments()[0]);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(final Class clazz) {  
        
        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。  
        Type genType = clazz.getGenericSuperclass();  
  
        if (!(genType instanceof ParameterizedType)) {  
           return Object.class;  
        }  
        //返回表示此类型实际类型参数的 Type 对象的数组。  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
  
  
        return (Class) params[0];  
    } 
}
