/*
 * The MIT License
 *
 * Copyright 2015 cappello.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Pete Cappello
 * @param <T> the type of objectList being permuted.
 */
public class Permutation<T>
{
    private Permutation<T> subPermutationEnumerator;
    private List<T> permutation;
    private List<T> subpermutation;
    private int nextIndex = 0;
    private T interleaveObject;

    final static Integer ONE = 1;
    final static Integer TWO = 2;

    public static void iterate( List<Integer> permutation, int k, String pad, Consumer<List<Integer>> consumer )
    {
        System.out.println( pad + "ENTRY: k: " + k + "  " +  permutation );
        for( int i = k; i < permutation.size(); i++ )
            {
                Collections.swap( permutation, i, k );
                System.out.println( pad + "FOR: i: " + i + " swapped p: " +  permutation );
                iterate( permutation, k + 1, pad + "   " , consumer );
                Collections.swap( permutation, k, i );
                System.out.println( pad + "FOR: i: " + i + " unswapped p: " +  permutation );
            }
        if ( k == permutation.size() - 1 )
            {
                consumer.accept( permutation );
            }
    }

    /**
     *
     * @param objectList the objectList being permuted is unmodified.
     * @throws java.lang.IllegalArgumentException when passed a null object list.
     */
    public Permutation( final List<T> objectList ) throws IllegalArgumentException
    {
        if ( objectList == null )
            {
                throw new IllegalArgumentException();
            }
        permutation = new ArrayList<>( objectList );
        if ( permutation.isEmpty() )
            {
                return;
            }
        subpermutation = new ArrayList<>( permutation );
        interleaveObject = subpermutation.remove( 0 );
        subPermutationEnumerator = new Permutation<>( subpermutation );
        subpermutation = subPermutationEnumerator.next();
    }

    /**
     * Produce the permutation permutation.
     * @return the permutation permutation as a List.
     * If none, returns null.
     * @throws java.lang.IllegalArgumentException  permutation() invoked when hasNext() is false.
     */
    public List<T> next() throws IllegalArgumentException
    {
        if ( permutation == null )
            {
                return null;
            }
        List<T> returnValue = new ArrayList<>( permutation );
        if ( permutation.isEmpty() )
            {
                permutation = null;
            }
        else if ( nextIndex < permutation.size() - 1)
            {
                T temp = permutation.get( nextIndex + 1 );
                permutation.set( nextIndex + 1, permutation.get( nextIndex ) );
                permutation.set( nextIndex++, temp );
            }
        else
            {
                subpermutation = subPermutationEnumerator.next();
                if ( subpermutation == null || subpermutation.isEmpty() )
                    {
                        permutation = null;
                    }
                else
                    {
                        permutation = new ArrayList<>( subpermutation );
                        permutation.add( 0, interleaveObject );
                        nextIndex = 0;
                    }
            }
        return returnValue;
    }
}
