/*
 * Copyright 2012 Joseph Spencer.
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
package com.spencernetdevelopment;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Joseph Spencer
 */
public class GroupedAssetTransactionTest {
   private GroupedAssetTransaction transaction;

   public GroupedAssetTransactionTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
      transaction = new GroupedAssetTransaction("js");
   }

   @After
   public void tearDown() {
   }

   //Before group is closed
   @Test
   public void constructor_allows_certain_types(){
      new GroupedAssetTransaction("css");
      new GroupedAssetTransaction("js");
   }
   @Test(expected = IllegalArgumentException.class)
   public void constructor_only_allows_certain_types(){
      new GroupedAssetTransaction("foo");
   }
   @Test
   public void compressed_by_default(){
      GroupedAssetTransaction groupedAssetTransaction =
         new GroupedAssetTransaction("js");
      assertTrue(groupedAssetTransaction.isCompressed());
   }
   @Test
   public void can_disable_compression(){
      GroupedAssetTransaction groupedAssetTransaction =
         new GroupedAssetTransaction("js", false);
      assertFalse(groupedAssetTransaction.isCompressed());
   }
   @Test
   public void can_get_type(){
      assertEquals("js", transaction.getType());
   }
   @Test
   public void can_add_url_before_group_is_closed(){
      transaction.addURL("/script");
      transaction.addURL("/script2");
   }
   @Test
   public void can_add_similar_urls_before_group_is_closed(){
      transaction.addURL("/script");
      transaction.addURL("/script");
   }
   @Test(expected = NullPointerException.class)
   public void add_null_url_before_group_is_closed_throws_exception(){
      transaction.addURL(null);
   }
   @Test(expected = IllegalArgumentException.class)
   public void add_empty_url_before_group_is_closed_throws_exception(){
      transaction.addURL("    \n \t ");
   }
   @Test
   public void get_identifier_closes_group(){
      transaction.addURL("script");
      transaction.getIdentifier();
      assertTrue(transaction.isClosed());
   }
   @Test(expected = IllegalStateException.class)
   public void can_not_get_array_when_no_urls_are_added(){
      transaction.toArray();
   }
   @Test(expected = IllegalStateException.class)
   public void group_must_have_urls_before_closing(){
      transaction.close();
   }
   @Test(expected = IllegalStateException.class)
   public void group_must_have_urls_before_getting_identifier(){
      transaction.getIdentifier();
   }

   //After group is closed
   @Test
   public void group_with_urls_can_close(){
      transaction.addURL("script");
      transaction.close();
   }
   @Test(expected = IllegalStateException.class)
   public void can_not_add_urls_after_closing(){
      transaction.addURL("script");
      transaction.close();
      transaction.addURL("script");
   }
   @Test(expected = IllegalStateException.class)
   public void can_not_add_urls_after_getting_identifier(){
      transaction.addURL("script");
      transaction.getIdentifier();
      transaction.addURL("script");
   }

   @Test
   public void can_get_array_when_closed(){
      transaction.addURL("script");
      transaction.addURL("script2");
      transaction.close();
      String[] array = transaction.toArray();
      assertEquals("script", array[0]);
      assertEquals("script2", array[1]);
   }
   /**
    * This probably never gets to toArray, but it's here to ensure that it will
    * never happen.
    */
   @Test(expected = IllegalStateException.class)
   public void must_add_urls_to_get_array(){
      transaction.toArray();
   }
   @Test
   public void can_get_identifier_when_urls_are_added(){
      transaction.addURL("script");
      transaction.addURL("script");
      String identifier = transaction.getIdentifier();
      System.out.println(identifier);
      assertEquals("a840c76592633e0af0704479fc1b11df", identifier);
   }
   @Test
   public void identifiers_avoid_collisions(){
      String attemptToDuplicate = "a840c76592633e0af0704479fc1b11df";
      String identifier;
      transaction.addURL("script");
      transaction.addURL("script");
      transaction.close();
      identifier = transaction.getIdentifier();
      assertEquals(attemptToDuplicate, identifier);

      transaction = new GroupedAssetTransaction("js");
      transaction.addURL("script_script");
      transaction.close();
      identifier = transaction.getIdentifier();
      assertFalse(attemptToDuplicate.equals(identifier));

      transaction = new GroupedAssetTransaction("js");
      transaction.addURL("scrip");
      transaction.addURL("tscript");
      transaction.close();
      identifier = transaction.getIdentifier();
      assertFalse(attemptToDuplicate.equals(identifier));
   }
   @Test
   public void can_determine_if_group_is_closed(){
      transaction.addURL("boo");
      assertFalse(transaction.isClosed());
      transaction.close();
      assertTrue(transaction.isClosed());
   }
   @Test
   public void wrapjs_should_be_available_from_a_method(){
      assertFalse("wrapjs was true.", transaction.shouldWrapJsInClosure());
      transaction = new GroupedAssetTransaction("js", false, true);
      assertTrue("wrapjs was false.", transaction.shouldWrapJsInClosure());
   }
}