#!/usr/bin/env sage

# ---------------------------------------------------------
# Generate a random 3-connected plane graph with n vertices
# and print its adjacency list representation to stdout
# (or write it to the file specified as second parameter).
#
# Example usage:
#   $ sage generate-random.sage 32
#   $ sage generate-random.sage 24 random-24.txt
#   $ sage generate-random.sage 12 > random-12.txt
#
# Requirements:
# - Sage <http://www.sagemath.org/>
# - Plantri <http://users.cecs.anu.edu.au/~bdm/plantri/>
#
# ---------------------------------------------------------

# ---------------------------------------------------------
# Settings
#
# This seed value is used for choosing some random graphs
# among a large set of available graphs. For reproducibility
# we use the following SEED value as a fixed 'random' value.
SEED = 256
#
# ---------------------------------------------------------


def get_embedded_graph(n):
    """
    Return the adjacency list embedding of a random 3-connected
    plane graph with n vertices generated using plantri.
    """
    gen = graphs.planar_graphs(n, minimum_connectivity=3)
    try:
        for skip in range(SEED * (1 + (n % 10))):
            gen.next()
    except StopIteration:
        # If there are not enough graphs to choose from,
        # we simply take the first one.
        gen = graphs.planar_graphs(n, minimum_connectivity=3)
    graph = gen.next()
    return graph.get_embedding()

def write_graph(filename, adjdict):
    """
    Write the adjacency list representation of a plane graph
    to a file with the specified name.
    """
    with open(filename, 'w') as out:
        for k,v in adjdict.items():
            out.write("{} {}\n".format(k, " ".join(map(str, v))))

def main():
    if len(sys.argv) < 2 or len(sys.argv) > 3:
        sys.exit("usage: {} n [filename]".format(sys.argv[0]))
    try:
        n = int(sys.argv[1])
    except ValueError:
        sys.exit("error: n must be an integer")
    if n < 4:
        sys.exit("error: n must not be smaller than 4")
    if n > 64:
        sys.exit("error: n must not be larger than 64")
    graph = get_embedded_graph(n)
    if len(sys.argv) == 3:
        filename = sys.argv[2]
        write_graph(filename, graph)
    else:
        for k,v in graph.items():
            print("{} {}".format(k, " ".join(map(str, v))))
    sys.exit()

if __name__ == "__main__":
    main()
