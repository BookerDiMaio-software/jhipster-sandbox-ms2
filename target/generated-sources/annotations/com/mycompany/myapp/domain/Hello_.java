package com.mycompany.myapp.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Hello.class)
public abstract class Hello_ {

	public static volatile SingularAttribute<Hello, String> firstName;
	public static volatile SingularAttribute<Hello, String> lastName;
	public static volatile SingularAttribute<Hello, Long> id;
	public static volatile SingularAttribute<Hello, String> salutation;

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String ID = "id";
	public static final String SALUTATION = "salutation";

}

