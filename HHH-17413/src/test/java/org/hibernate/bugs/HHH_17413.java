/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.entity.ChildAEntity;
import org.hibernate.bugs.entity.ChildBEntity;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Arrays;

import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

/**
 * The issue happens when query contains usage of TYPE(...) function with CASE statement and another column in SELECT
 * clause.
 *
 * <p>{@link HHH_17413#testSingleColumnSelect() First test}:
 * Test query contains only CASE TYPE(...) in SELECT clause.
 *
 * <p>{@link HHH_17413#testSingleColumnSelect() Second test}:
 * Test query contains ID column and the same CASE TYPE(...) in SELECT clause.
 *
 * @see <a href="https://hibernate.atlassian.net/browse/HHH-17413">HHH-17413</a>
 */
public class HHH_17413 extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { ChildAEntity.class, ChildBEntity.class };
	}

	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(FORMAT_SQL, Boolean.TRUE.toString());
		configuration.setProperty(GENERATE_STATISTICS, Boolean.FALSE.toString());
	}

	/**
	 * PASS.
	 *
	 * <p>Test query contains only CASE TYPE(...) in SELECT clause.
	 */
	@Test
	public void testSingleColumnSelect() {
		final var query = "SELECT " + caseSelect() + " FROM AbstractParentEntity p";
		caseWhenWithTypeTest(query);
	}

	/**
	 * FAIL.
	 *
	 * <p>Test query contains ID column and the same CASE TYPE(...) in SELECT clause.
	 */
	@Test
	public void testMultiColumnSelect() {
		final var query = "SELECT p.id, " + caseSelect() + " FROM AbstractParentEntity p";
		caseWhenWithTypeTest(query);
	}

	// Creates CASE clause used in both tests
	private static String caseSelect() {
		return "CASE TYPE(p) " +
					"WHEN " + ChildAEntity.class.getName() + " THEN 'A' " +
					"WHEN " + ChildBEntity.class.getName() + " THEN 'B' " +
					"ELSE NULL " +
				"END";
	}

	// Run same test with different testQuery
	private void caseWhenWithTypeTest(String testQuery) {
		transactional(() -> {
			session.persist(createChildA());
			session.persist(createChildB());

			final var resultList = session.createQuery(testQuery, Object[].class).getResultList();

			for (var result : resultList) {
				System.out.println(Arrays.toString(result));
			}
		});
	}

	private static ChildAEntity createChildA() {
		final var childA = new ChildAEntity();
		childA.setAttributeA("Attribute A");
		return childA;
	}

	private static ChildBEntity createChildB() {
		final var childB = new ChildBEntity();
		childB.setAttributeB("Attribute B");
		return childB;
	}

	private void transactional(Runnable transactionalCodeBlock) {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		transactionalCodeBlock.run();

		tx.commit();
		s.close();
	}
}
