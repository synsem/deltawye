#! /usr/bin/env python3

# ---------------------------------------------------------
# Generate a planar embedding in adjacency list format
# of a cylindrical graph with n columns and m rows.
#
# Example usage:
# $ generate-cylinder.py 3 3 > cylinder-3x3.txt
#
# ---------------------------------------------------------

import sys


def createPlaneCylindricalGraph(n, m):
    """
    Create a planar embedding of a (n x m) cylindrical graph
    (adjacency list format).
    """
    def right(x, y):
        return ((x+1) % n, y)
    def left(x, y):
        return ((x-1) % n, y)
    def up(x, y):
        return (x, (y-1) % m)
    def down(x, y):
        return (x, (y+1) % m)

    def first(x, y):
        return [(x, y), right(x, y), down(x, y), left(x, y)]
    def middle(x, y):
        return [(x, y), right(x, y), down(x, y), left(x, y), up(x, y)]
    def last(x, y):
        return [(x, y), right(x, y), left(x, y), up(x, y)]

    firstRow = [first(i,0) for i in range(n)]
    middleRows = [middle(i,j) for i in range(n) for j in range(1,m-1)]
    lastRow = [last(i,m-1) for i in range(n)]
    rows = firstRow + middleRows + lastRow

    vertices = [(i,j) for j in range(m) for i in range(n)]
    relabel = dict(zip(vertices, range(1, 1+len(vertices))))
    return list(map(lambda xs: list(map(lambda v: relabel[v], xs)), rows))


def main():
    if len(sys.argv) != 3:
        sys.exit("usage: {} n m".format(sys.argv[0]))
    try:
        n = int(sys.argv[1])
        m = int(sys.argv[2])
    except ValueError:
        sys.exit("error: n and m must be integers")
    if n < 3:
        sys.exit("error: n must not be smaller than 3 (to avoid loops)")
    if m < 2:
        sys.exit("error: m must not be smaller than 2")
    graph = createPlaneCylindricalGraph(n, m)
    for l in graph:
        print(" ".join(map(str, l)))
    sys.exit()

if __name__ == "__main__":
    main()
