#! /usr/bin/env python3

# ---------------------------------------------------------
# Generate a planar embedding in adjacency list format
# of a wheel graph with n vertices.
#
# Example usage:
# $ generate-wheel.py 8 > wheel-8.txt
#
# ---------------------------------------------------------

import sys


def createPlaneWheelGraph(n):
    """
    Create a planar embedding of a wheel graph with n vertices
    (adjacency list format).
    """
    m = n-1 # number of vertices in cycle
    center = [m] + list(range(m))
    cycle = [[x, m, (x-1) % m, (x+1) % m] for x in range(m)]
    return cycle + [center]

def main():
    if len(sys.argv) != 2:
        sys.exit("usage: {} n".format(sys.argv[0]))
    try:
        n = int(sys.argv[1])
    except ValueError:
        sys.exit("error: n must be an integer")
    if n < 4:
        sys.exit("error: n must not be smaller than 4")
    graph = createPlaneWheelGraph(n)
    for l in graph:
        print(" ".join(map(str, l)))
    sys.exit()

if __name__ == "__main__":
    main()
