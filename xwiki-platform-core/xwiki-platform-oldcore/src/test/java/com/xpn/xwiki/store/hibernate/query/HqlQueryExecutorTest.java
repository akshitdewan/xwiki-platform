/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.store.hibernate.query;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.query.QueryExecutor;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.xpn.xwiki.store.hibernate.HibernateSessionFactory;

/**
 * Tests for {@link HqlQueryExecutor}
 * 
 * @version $Id$
 */
public class HqlQueryExecutorTest
{
    @Rule
    public MockitoComponentMockingRule<QueryExecutor> mocker = new MockitoComponentMockingRule<QueryExecutor>(
        HqlQueryExecutor.class);

    /**
     * The component under test.
     */
    private HqlQueryExecutor executor;

    @Before
    public void configure() throws Exception
    {
        HibernateSessionFactory sessionFactory = this.mocker.getInstance(HibernateSessionFactory.class);
        when(sessionFactory.getConfiguration()).thenReturn(new Configuration());

        this.executor = (HqlQueryExecutor) this.mocker.getComponentUnderTest();
    }

    @Test
    public void completeShortStatementWhenEmpty()
    {
        assertEquals("select doc.fullName from XWikiDocument doc ", executor.completeShortFormStatement(""));
    }

    @Test
    public void completeShortStatementStartingWithWhere()
    {
        assertEquals("select doc.fullName from XWikiDocument doc where doc.author='XWiki.Admin'",
            executor.completeShortFormStatement("where doc.author='XWiki.Admin'"));
    }

    @Test
    public void completeShortStatementStartingWithFrom()
    {
        assertEquals("select doc.fullName from XWikiDocument doc , BaseObject obj where doc.fullName=obj.name "
            + "and obj.className='XWiki.MyClass'", executor.completeShortFormStatement(", BaseObject obj where "
            + "doc.fullName=obj.name and obj.className='XWiki.MyClass'"));
    }

    @Test
    public void completeShortStatementStartingWithOrderBy()
    {
        assertEquals("select doc.fullName from XWikiDocument doc order by doc.date desc",
            executor.completeShortFormStatement("order by doc.date desc"));
    }

    @Test
    public void completeShortStatementPassingAnAlreadyCompleteQuery()
    {
        assertEquals("select doc.fullName from XWikiDocument doc order by doc.date desc",
            executor.completeShortFormStatement("select doc.fullName from XWikiDocument doc order by doc.date desc"));
    }

    @Test
    public void completeShortStatementPassingAQueryOnSomethingElseThanADocument()
    {
        Assert.assertEquals("select lock.docId from XWikiLock as lock ",
            executor.completeShortFormStatement("select lock.docId from XWikiLock as lock "));
    }
}
